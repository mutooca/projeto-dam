package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.PerfilItem;
import com.anunciosloc.anunciosloc_server.dto.PerfilRequest;
import com.anunciosloc.anunciosloc_server.dto.PerfilResponse;
import com.anunciosloc.anunciosloc_server.model.PerfilUtilizador;
import com.anunciosloc.anunciosloc_server.model.SaldoUtilizador;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.PerfilUtilizadorRepository;
import com.anunciosloc.anunciosloc_server.repository.SaldoUtilizadorRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Os atributos de perfil são geridos e persistidos inteiramente no anunciosloc-server
 * (BD "anuncio"), não na infrastructura-server. Isto porque o perfil é um conceito global
 * do utilizador (partilhado por todos os utilizadores, independente de infraestrutura),
 * ao contrário de locais/anúncios que pertencem a uma infraestrutura específica.
 *
 * Quando a infra precisa do perfil para aplicar a política whitelist/blacklist na entrega
 * de anúncios, o anunciosloc-server envia-o como parte do próprio pedido SOAP
 * (ver AnuncioService.receberAnuncios) — a infra deixa de o ler de uma BD própria.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilUtilizadorRepository perfilRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final SaldoUtilizadorRepository saldoUtilizadorRepository;

    /**
     * Saldo de pontos ganhos quando outros utilizadores abrem os anúncios deste utilizador
     * (ver AnuncioService.marcarComoLido). Vive aqui no anunciosloc-server para que o perfil
     * mostre sempre o valor mais atual sem depender de uma chamada SOAP à infraestrutura.
     */
    public int obterSaldo(String email) {
        Utilizador utilizador = obterUtilizador(email);
        return saldoUtilizadorRepository.findByUtilizador(utilizador)
                .map(SaldoUtilizador::getSaldo)
                .orElse(0);
    }

    @Transactional
    public String adicionarPerfil(PerfilRequest request) {
        log.info(" [PERFIL] Adicionando perfil para: {} ({} atributo(s))",
                request.getEmail(), request.getPerfil() != null ? request.getPerfil().size() : 0);

        Utilizador utilizador = obterUtilizador(request.getEmail());

        perfilRepository.deleteByUtilizador(utilizador);
        // Força a execução imediata do DELETE. Sem isto, o Hibernate agenda os INSERTs
        // seguintes antes do DELETE (ordem de flush por defeito: inserts, updates, deletes),
        // o que viola a unique constraint (id_utilizador, chave) quando se reenvia uma chave
        // já existente como parte da lista completa do perfil.
        perfilRepository.flush();

        for (PerfilItem item : request.getPerfil()) {
            perfilRepository.save(PerfilUtilizador.builder()
                    .utilizador(utilizador)
                    .chave(item.getChave())
                    .valor(item.getValor())
                    .build());
            log.info("   {} = {}", item.getChave(), item.getValor());
        }

        log.info(" Perfil atualizado com sucesso na BD do anunciosloc-server para: {}", request.getEmail());
        return "Perfil atualizado com sucesso!";
    }

    public PerfilResponse consultarPerfil(String email) {
        log.info(" [PERFIL] Consultando perfil para: {}", email);

        Utilizador utilizador = utilizadorRepository.findByEmail(email).orElse(null);
        if (utilizador == null) {
            log.warn(" Utilizador não encontrado: {}", email);
            return PerfilResponse.builder()
                    .email(email)
                    .perfil(List.of())
                    .mensagem("Utilizador não encontrado")
                    .build();
        }

        List<PerfilItem> perfil = perfilRepository.findByUtilizador(utilizador).stream()
                .map(p -> PerfilItem.builder().chave(p.getChave()).valor(p.getValor()).build())
                .collect(Collectors.toList());

        return PerfilResponse.builder()
                .email(email)
                .perfil(perfil)
                .mensagem("Perfil obtido com sucesso")
                .build();
    }

    @Transactional
    public String removerChavePerfil(String email, String chave) {
        log.info(" [PERFIL] Removendo chave '{}' para: {}", chave, email);

        Utilizador utilizador = obterUtilizador(email);

        boolean existia = perfilRepository.findByUtilizadorAndChave(utilizador, chave).isPresent();
        if (!existia) {
            log.warn("   Chave '{}' não encontrada para: {}", chave, email);
            throw new RuntimeException("Chave '" + chave + "' não encontrada");
        }

        perfilRepository.deleteByUtilizadorAndChave(utilizador, chave);
        log.info(" Chave '{}' removida com sucesso para: {}", chave, email);
        return "Chave '" + chave + "' removida com sucesso!";
    }

    /**
     * Catálogo global: pares chave=valor distintos criados por QUALQUER utilizador.
     * Serve para o utilizador escolher atributos já existentes (em vez de digitar) tanto
     * no perfil como nas restrições de postagem.
     */
    public List<PerfilItem> listarTodosPerfis() {
        log.info(" [PERFIL] Listando catálogo global de atributos");

        List<PerfilUtilizador> todos = perfilRepository.findAll();
        log.info("   Total de registos de perfil na BD: {}", todos.size());

        LinkedHashMap<String, PerfilItem> distintos = new LinkedHashMap<>();
        for (PerfilUtilizador registo : todos) {
            if (registo.getChave() == null || registo.getChave().isBlank()
                    || registo.getValor() == null || registo.getValor().isBlank()) {
                continue;
            }
            String key = (registo.getChave().trim() + "=" + registo.getValor().trim()).toLowerCase();
            distintos.putIfAbsent(key, PerfilItem.builder()
                    .chave(registo.getChave().trim())
                    .valor(registo.getValor().trim())
                    .build());
        }

        List<PerfilItem> resultado = new ArrayList<>(distintos.values());
        resultado.sort(Comparator
                .comparing(PerfilItem::getChave, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(PerfilItem::getValor, String.CASE_INSENSITIVE_ORDER));

        log.info(" {} pares chave=valor distintos no catálogo global", resultado.size());
        return resultado;
    }

    /**
     * Usado pelo AnuncioService para anexar o perfil do destinatário ao pedido SOAP de
     * entrega, já que a infra deixou de ter acesso direto à BD de perfis.
     */
    public List<PerfilItem> obterPerfilParaEntrega(String email) {
        return consultarPerfil(email).getPerfil();
    }

    private Utilizador obterUtilizador(String email) {
        return utilizadorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
    }
}
