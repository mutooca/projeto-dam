package com.uan.anunciosloc.infrastructura_server.uddi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;


@Slf4j
@Component
public class UddiRegistrarClient {

    @Value("${uddi.server-url:http://localhost:9000}")
    private String uddiServerUrl;

    @Value("${infra.nome:D01_Infrastructure1}")
    private String infraNome;

    @Value("${infra.public-url:http://localhost:8081}")
    private String publicUrl;

    private final WebClient.Builder webClientBuilder;

    public UddiRegistrarClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    //Registar no UDDI com retry
    @SuppressWarnings("null")
    public void registar(String serviceUrl) {
    Map<String, String> body = Map.of(
        "serviceName", infraNome,
        "serviceUrl",  serviceUrl
    );

    log.info("A registar '{}' no UDDI: {}", infraNome, uddiServerUrl);

    for (int tentativa = 1; tentativa <= 3; tentativa++) {
        try {
            String resposta = webClientBuilder
                    .baseUrl(uddiServerUrl)
                    .build()
                    .post()
                    .uri("/api/uddi/register")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Registo UDDI bem sucedido: {}", resposta);
            return;

        } catch (Exception e) {
            log.warn("Tentativa {}/3 falhou: {}", tentativa, e.getMessage());
            if (tentativa < 3) {
                try {
                    Thread.sleep(tentativa * 2000L);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    log.error("Não foi possível registar no UDDI após 3 tentativas.");
}
    //Cancelar registo ao desligar
    @SuppressWarnings("null")
    public void cancelarRegisto() {
        try {
            webClientBuilder
                    .baseUrl(uddiServerUrl)
                    .build()
                    .delete()
                    .uri("/api/uddi/register/{nome}", infraNome)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Registo UDDI cancelado para '{}'", infraNome);

        } catch (Exception e) {
            log.warn("Não foi possível cancelar registo UDDI: {}", e.getMessage());
        }
    }

    //Heartbeat chama periodicamente para manter o registo activo
    @SuppressWarnings("null")
    public void ping() {
        try {
            webClientBuilder
                    .baseUrl(uddiServerUrl)
                    .build()
                    .post()
                    .uri("/api/uddi/ping/{nome}", infraNome)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.debug("Heartbeat UDDI enviado: {}", infraNome);

        } catch (Exception e) {
            log.warn("Heartbeat UDDI falhou: {}", e.getMessage());
        }
    }
}
