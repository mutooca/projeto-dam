package com.uan.anunciosloc.infrastructura_server.config;

import com.uan.anunciosloc.infrastructura_server.soap.InfraestruturaServiceImpl;
import com.uan.anunciosloc.infrastructura_server.uddi.UddiRegistrarClient;
import jakarta.annotation.PreDestroy;
import jakarta.xml.ws.Endpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class InfraConfig {

    private final InfraestruturaServiceImpl infraService;
    private final UddiRegistrarClient    uddiClient;

    @Value("${infra.nome:D01_Infrastructure1}")
    private String infraNome;

    @Value("${infra.public-url:http://localhost:8081}")
    private String publicUrl;

    
    private Endpoint endpoint;

    
    @Value("${infra.soap-port:8091}")
private String soapPort;

@Bean
public ApplicationRunner iniciar() {
    return args -> {

       
        String endpointUrl = "http://localhost:" + soapPort + "/ws/InfrastructureService";
        endpoint = Endpoint.publish(endpointUrl, infraService);

        log.info("Endpoint SOAP publicado em: {}", endpointUrl);
        log.info("WSDL disponível em: {}?wsdl", endpointUrl);

        
        uddiClient.registar(endpointUrl);
    };
}
    @Scheduled(fixedDelay = 120_000)
    public void heartbeat() {
        uddiClient.ping();
    }

    
    @PreDestroy
    public void aoDesligar() {
        log.info("A desligar infraestrutura '{}'...", infraNome);

        uddiClient.cancelarRegisto();

        if (endpoint != null && endpoint.isPublished()) {
            endpoint.stop();
            log.info("Endpoint SOAP parado");
        }
    }
}