package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.uddi.dto.AnuncioInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import com.anunciosloc.anunciosloc_server.service.LocalService;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioRequestSOAP;
import com.anunciosloc.anunciosloc_server.dto.MarcarLidoRequest;
import com.anunciosloc.anunciosloc_server.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final LocalService localService;

    @PostMapping("/postar")
    public ResponseEntity<?> postarAnuncio(@Valid @RequestBody PostarAnuncioRequestSOAP request) {
        try {
            String resultado = anuncioService.postarAnuncio(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
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
            LocalInfoSOAP localMaisProximo = locaisProximos.get(0);

            if (localMaisProximo == null || localMaisProximo.getIdLocal() == null) {
                log.warn(" Local mais próximo tem ID null!");
                return ResponseEntity.ok(List.of());
            }

            log.info("  Local MAIS PRÓXIMO: {} (distância: {}m)",
                    localMaisProximo.getNome(),
                    localMaisProximo.getDistancia() != null
                            ? Math.round(localMaisProximo.getDistancia())
                            : "desconhecida");

            if (localMaisProximo.getDistancia() != null &&
                    localMaisProximo.getDistancia() > 50.0) {
                log.info(" Utilizador está a {}m do local (mínimo: 50m)",
                        Math.round(localMaisProximo.getDistancia()));
                return ResponseEntity.ok(List.of());
            }

            List<AnuncioInfoSOAP> anuncios = anuncioService.receberAnuncios(
                    email,
                    localMaisProximo.getIdLocal());

            log.info(" {} anúncios encontrados no local '{}'",
                    anuncios.size(), localMaisProximo.getNome());

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
            MensagemResponse resultado = anuncioService.marcarComoLido(
                    request.getIdAnuncio(),
                    request.getEmailUtilizador());

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error(" Erro ao marcar como lido: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

}