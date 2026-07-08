package com.uan.anunciosloc.infrastructura_server.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacaoService {

    @Value("${anunciosloc.server-url:http://localhost:8080}")
    private String anunciosLocUrl;

    private final WebClient.Builder webClientBuilder;
    

    
    @SuppressWarnings("null")
    public void sincronizarSaldos() {
        log.info("A sincronizar saldos com anunciosloc_server...");

        try {
            List<Map<String, Object>> saldos = webClientBuilder
                    .baseUrl(anunciosLocUrl)
                    .build()
                    .get()
                    .uri("/api/utilizadores/saldos/todos")
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            if (saldos == null || saldos.isEmpty()) {
                log.warn("Nenhum saldo recebido do anunciosloc_server");
                return;
            }

           /* * for (Map<String, Object> saldo : saldos) {
                String idUtilizador = (String) saldo.get("idUtilizador");
                float valor = ((Number) saldo.get("saldo")).floatValue();
                int versao = ((Number) saldo.get("versao")).intValue();

                estadoService.escreverSaldo(idUtilizador, valor, versao);
            }*/

            log.info("Sincronização concluída — {} saldos carregados", saldos.size());

        } catch (Exception e) {
            log.warn("Não foi possível sincronizar saldos: {}. " +
                     "Réplicas começam vazias.", e.getMessage());
        }
    }
}