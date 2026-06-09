package com.anunciosloc.anunciosloc_server.uddi;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.anunciosloc.anunciosloc_server.uddi.dto.UddiRecord;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class UddiClient {

    @Value("${uddi.server-url:http://localhost:9000}")
    private String uddiServerUrl;

    @Value("${infra.group-prefix:D01}")
    private String groupPrefix;

    private final WebClient.Builder webClientBuilder;

    public UddiClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

   
 
    // Chama: GET /api/uddi/lookup?prefixo=D01_Infrastructure
    @SuppressWarnings("null")
    public List<UddiRecord> descobrirInfraestruturas() {
        try {
            String prefixo = groupPrefix + "_Infrastructure";

            List<UddiRecord> infras = webClientBuilder
                    .baseUrl(uddiServerUrl)
                    .build()
                    .get()
                    .uri(u -> u.path("/api/uddi/lookup")
                               .queryParam("prefixo", prefixo)
                               .build())
                    .retrieve()
                    .bodyToFlux(UddiRecord.class)
                    .collectList()
                    .block();

            if (infras == null || infras.isEmpty()) {
                log.warn("Nenhuma infraestrutura encontrada no UDDI");
                return Collections.emptyList();
            }

            log.debug("UDDI: {} infraestrutura(s) encontrada(s)", infras.size());
            return infras;

        } catch (Exception e) {
            log.error("Erro ao contactar UDDI: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    //Obter URL de uma infraestrutura pelo nome exato
    @SuppressWarnings("null")
    public String obterUrlInfraestrutura(String serviceName) {
        try {
            UddiRecord record = webClientBuilder
                    .baseUrl(uddiServerUrl)
                    .build()
                    .get()
                    .uri("/api/uddi/lookup/{nome}", serviceName)
                    .retrieve()
                    .bodyToMono(UddiRecord.class)
                    .block();

            if (record == null) {
                log.warn("Infraestrutura '{}' não encontrada no UDDI", serviceName);
                return null;
            }

            return record.getServiceUrl();

        } catch (Exception e) {
            log.error("Erro ao obter URL de '{}': {}", serviceName, e.getMessage());
            return null;
        }
    }

    public List<String> obterUrlsInfraestruturas() {
        return descobrirInfraestruturas().stream()
                .map(UddiRecord::getServiceUrl)
                .toList();
    }
}