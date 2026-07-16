package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.uddi.dto.AnuncioInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ResultadoLeituraSOAP;
import com.anunciosloc.anunciosloc_server.security.KerberosAuthInterceptor;
import com.anunciosloc.anunciosloc_server.service.LocalService;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioRequestSOAP;
import com.anunciosloc.anunciosloc_server.dto.MarcarLidoRequest;
import com.anunciosloc.anunciosloc_server.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final LocalService localService;

    @PostMapping("/postar")
    public ResponseEntity<?> postarAnuncio(@Valid @RequestBody PostarAnuncioRequestSOAP request) {
        log.info(" [ANUNCIOSLOC] Pedido REST para postar anuncio");
        log.info("   Autor: {}", request.getEmailAutor());
        log.info("   Local remoto: {}", request.getIdLocal());
        log.info("   Titulo: {}", request.getTitulo());
        log.info("   Categoria: {}", request.getCategoria());
        log.info("   Tipo politica: {}", request.getTipoPolitica());
        log.info("   Politica filtro: {}", request.getPoliticaFiltro());
        log.info("   Visivel de: {}", request.getVisivelDe());
        log.info("   Visivel ate: {}", request.getVisivelAte());
        try {
            String resultado = anuncioService.postarAnuncio(request);
            log.info(" [ANUNCIOSLOC] Resultado do post de anuncio: {}", resultado);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error(" [ANUNCIOSLOC] Erro ao postar anuncio", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/receber-por-localizacao")
    public ResponseEntity<?> receberAnunciosPorLocalizacao(
            @RequestParam String email,
            @RequestParam Double lat,
            @RequestParam Double lon) {

        log.info(" [ANUNCIOSLOC] Recebendo anúncios para: {} em lat={}, lon={}",
                email, lat, lon);

        try {
            List<LocalInfoSOAP> locaisProximos = localService.listarLocais(lat, lon);

            if (locaisProximos.isEmpty()) {
                log.info(" Nenhum local próximo encontrado");
                return ResponseEntity.ok(List.of());
            }

            // Não basta olhar só para o local "mais próximo": pode haver vários locais a
            // distâncias iguais/semelhantes (inclusive coordenadas idênticas, comuns em testes
            // e em locais reais próximos uns dos outros) e o anúncio pode estar associado a
            // QUALQUER um deles dentro do alcance. Considerar apenas o primeiro fazia com que
            // anúncios de outros locais em alcance nunca fossem encontrados.
            List<LocalInfoSOAP> locaisEmAlcance = locaisProximos.stream()
                    .filter(l -> l.getIdLocal() != null
                            && l.getDistancia() != null
                            && l.getDistancia() <= 100.0)
                    .collect(java.util.stream.Collectors.toList());

            log.info("  {} local(is) dentro do alcance (<=100m) de {} candidato(s) próximo(s)",
                    locaisEmAlcance.size(), locaisProximos.size());

            if (locaisEmAlcance.isEmpty()) {
                LocalInfoSOAP maisProximo = locaisProximos.get(0);
                log.info(" Utilizador está a {}m do local mais próximo '{}' (mínimo: 100m)",
                        maisProximo.getDistancia() != null ? Math.round(maisProximo.getDistancia()) : "desconhecida",
                        maisProximo.getNome());
                return ResponseEntity.ok(List.of());
            }

            LinkedHashMap<String, AnuncioInfoSOAP> anunciosPorId = new LinkedHashMap<>();
            for (LocalInfoSOAP local : locaisEmAlcance) {
                List<AnuncioInfoSOAP> anunciosDoLocal = anuncioService.receberAnuncios(email, local.getIdLocal());
                log.info("   Local '{}' (distância: {}m): {} anúncio(s) elegível(eis)",
                        local.getNome(), Math.round(local.getDistancia()), anunciosDoLocal.size());
                for (AnuncioInfoSOAP anuncio : anunciosDoLocal) {
                    anunciosPorId.putIfAbsent(anuncio.getId(), anuncio);
                }
            }

            List<AnuncioInfoSOAP> anuncios = new ArrayList<>(anunciosPorId.values());
            log.info(" {} anúncio(s) elegível(eis) no total, agregados de {} local(is) em alcance",
                    anuncios.size(), locaisEmAlcance.size());

            return ResponseEntity.ok(anuncios);

        } catch (Exception e) {
            log.error("Erro ao receber anúncios por localização", e);
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/receber/{email}/{localId}")
    public ResponseEntity<?> receberAnuncios(
            @PathVariable String email,
            @PathVariable String localId) {
        try {
            List<AnuncioInfoSOAP> anuncios = anuncioService.receberAnuncios(email, localId);
            return ResponseEntity.ok(anuncios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{idAnuncio}")
    public ResponseEntity<?> eliminarAnuncio(
            @PathVariable String idAnuncio,
            @RequestParam(required = false) String emailUtilizador,
            @RequestParam(required = false) String role,
            HttpServletRequest httpRequest) {

        String authenticatedEmail = (String) httpRequest.getAttribute(KerberosAuthInterceptor.ATTR_AUTHENTICATED_EMAIL);

        log.info(" [ANUNCIOSLOC] Pedido para eliminar anúncio: {} | autenticado={} | emailUtilizador(cliente)={} | role(cliente, ignorada)={}",
                idAnuncio, authenticatedEmail, emailUtilizador, role);

        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            log.error(" [ANUNCIOSLOC] Não foi possível resolver a identidade autenticada para eliminar anúncio {}", idAnuncio);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "sucesso", false,
                    "mensagem", "Não foi possível confirmar a identidade autenticada. Faça login novamente."));
        }

        if (emailUtilizador != null && !emailUtilizador.isBlank()
                && !emailUtilizador.equalsIgnoreCase(authenticatedEmail)) {
            log.warn(" [ANUNCIOSLOC] emailUtilizador enviado pelo cliente ({}) não corresponde à identidade autenticada ({}). Pedido rejeitado.",
                    emailUtilizador, authenticatedEmail);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "sucesso", false,
                    "mensagem", "O utilizador indicado não corresponde à sessão autenticada."));
        }

        try {
            String resultado = anuncioService.eliminarAnuncio(idAnuncio, authenticatedEmail);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", resultado));
        } catch (Exception e) {
            log.error(" Erro ao eliminar anúncio: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()));
        }
    }

    @GetMapping("/meus")
    public ResponseEntity<?> listarMeusAnuncios(
            @RequestParam String email) {

        log.info(" [ANUNCIOSLOC] Listando meus anúncios para: {}", email);

        try {
            List<AnuncioInfoSOAP> anuncios = anuncioService.listarMeusAnuncios(email);

            log.info(" {} anúncios encontrados para: {}", anuncios.size(), email);

            return ResponseEntity.ok(anuncios);

        } catch (Exception e) {
            log.error(" Erro ao listar meus anúncios: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/marcar-lido")
    public ResponseEntity<?> marcarComoLido(
            @RequestBody MarcarLidoRequest request) {

        log.info(" [ANUNCIOSLOC] Marcando anúncio como lido");
        log.info("   ID Anúncio: {}", request.getIdAnuncio());
        log.info("   Utilizador: {}", request.getEmailUtilizador());

        try {
            ResultadoLeituraSOAP resultado = anuncioService.marcarComoLido(
                    request.getIdAnuncio(),
                    request.getEmailUtilizador());

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error(" Erro ao marcar como lido: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

}
