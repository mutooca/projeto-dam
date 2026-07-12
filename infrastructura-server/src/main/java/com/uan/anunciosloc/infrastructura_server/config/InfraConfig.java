package com.uan.anunciosloc.infrastructura_server.config;

import com.uan.anunciosloc.infrastructura_server.repository.*;
import com.uan.anunciosloc.infrastructura_server.service.InatividadeService;
import com.uan.anunciosloc.infrastructura_server.service.InfraEstadoService;
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

    private final UddiRegistrarClient uddiClient;
    private final InfraEstadoService infraEstadoService;
    private final InatividadeService inatividadeService;
    private final LocalRepository localRepository;
    private final AnuncioRepository anuncioRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final PerfilUtilizadorRepository perfilRepository;
    private final EntregaAnuncioRepository entregaRepository;
    private final CoordenadaGpsRepository gpsRepository;
    private final CoordenadaWifiRepository wifiRepository;

    @Value("${infra.nome:D01_Infrastructure1}")
    private String infraNome;

    @Value("${infra.public-url:http://localhost:8081}")
    private String publicUrl;

    @Value("${infra.soap-port:8091}")
    private String soapPort;

    private Endpoint endpoint;

    @Bean
    public ApplicationRunner iniciar() {
        return args -> {

            InfraestruturaServiceImpl service = new InfraestruturaServiceImpl(
                infraEstadoService,      // 1 - InfraEstadoService
                localRepository,         // 2 - LocalRepository
                anuncioRepository,       // 3 - AnuncioRepository
                saldoRepository,         // 4 - SaldoUtilizadorRepository
                perfilRepository,        // 5 - PerfilUtilizadorRepository
                entregaRepository,       // 6 - EntregaAnuncioRepository
                gpsRepository,           // 7 - CoordenadaGpsRepository
                wifiRepository,          // 8 - CoordenadaWifiRepository
                inatividadeService       // 9 - InatividadeService
        );;

            String endpointUrl = "http://localhost:" + soapPort + "/ws/InfrastructureService";

            endpoint = Endpoint.publish(endpointUrl, service);

            log.info("═══════════════════════════════════════════════════════════════");
            log.info(" Endpoint SOAP publicado em: {}", endpointUrl);
            log.info(" WSDL disponível em: {}?wsdl", endpointUrl);
            log.info("═══════════════════════════════════════════════════════════════");

            uddiClient.registar(endpointUrl);
        };
    }

    @Scheduled(fixedDelayString = "${heartbeat.interval:60000}") 
    public void enviarHeartbeat() {
        uddiClient.ping();
    }

    @PreDestroy
    public void aoDesligar() {
        log.info(" A desligar infraestrutura '{}'...", infraNome);

        if (endpoint != null && endpoint.isPublished()) {
            endpoint.stop();
            log.info("Endpoint SOAP parado");
        }

        uddiClient.cancelarRegisto();
    }
    
}