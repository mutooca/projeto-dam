package com.uan.anunciosloc.infrastructura_server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uan.anunciosloc.infrastructura_server.model.Infraestrutura;
import com.uan.anunciosloc.infrastructura_server.model.SaldoUtilizador;
import com.uan.anunciosloc.infrastructura_server.repository.InfraestruturaRepository;
import com.uan.anunciosloc.infrastructura_server.repository.SaldoUtilizadorRepository;
import com.uan.anunciosloc.infrastructura_server.uddi.UddiRegistrarClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraEstadoService {

    private final InfraestruturaRepository infraRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final UddiRegistrarClient uddiRegistrarClient;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${infra.nome:D01_Infrastructure1}")
    private String infraNome;

    @Value("${infra.public-url:http://localhost:8091}")
    private String publicUrl;

    @Value("${infra.latitude:-8.8368}")
    private double latitudeConfig;

    @Value("${infra.longitude:13.2343}")
    private double longitudeConfig;

    @Value("${infra.raio:1000000.0}")
    private double raioConfig;

    @Value("${anunciosloc.server-url:http://localhost:8080}")
    private String anuncioslocUrl;

    @Value("${infra.id:}")
    private String infraIdConfig;

    @Value("${infra.capacidade:100}")
    private int capacidadeConfig;

    @Value("${infra.bonus-entrega:2}")
    private int bonusEntregaConfig;

    @Value("${infra.custo-post:1}")
    private int custoPostConfig;

    private Infraestrutura infraCache;

    @SuppressWarnings("null")
    @PostConstruct
    @Transactional
    public void init() {
        log.info("═══════════════════════════════════════════════════════════════");
        log.info(" INFRA-SERVER: Inicializando...");
        log.info("   Nome: {}", infraNome);
        log.info("   URL: {}", publicUrl);
        log.info("   Cobertura: lat={}, lon={}, raio={}m",
                latitudeConfig, longitudeConfig, raioConfig);

        // Gerar ou usar ID fornecido
        UUID idInfra;
        if (infraIdConfig != null && !infraIdConfig.isEmpty()) {
            try {
                idInfra = UUID.fromString(infraIdConfig);
                log.info("   ID (fornecido): {}", idInfra);
            } catch (IllegalArgumentException e) {
                idInfra = UUID.randomUUID();
                log.info("   ID (gerado automaticamente): {}", idInfra);
            }
        } else {
            idInfra = UUID.randomUUID();
            log.info("   ID (gerado automaticamente): {}", idInfra);
        }

        log.info("═══════════════════════════════════════════════════════════════");

        infraCache = infraRepository.findByNome(infraNome).orElse(null);

        if (infraCache == null) {
            log.info(" Criando nova infraestrutura...");

            Infraestrutura novaInfra = Infraestrutura.builder()
                    .idInfraestrutura(idInfra)
                    .nome(infraNome)
                    .urlEndpoint(publicUrl)
                    .latitude(latitudeConfig)
                    .longitude(longitudeConfig)
                    .raio(raioConfig)
                    .capacidade(capacidadeConfig)
                    .bonusEntrega(bonusEntregaConfig)
                    .custoPost(custoPostConfig)
                    .dataRegisto(LocalDateTime.now())
                    .ativa(true)
                    .totalLocais(0)
                    .totalAnuncios(0)
                    .totalEntregas(0)
                    .totalConexoes(0)
                    .build();

            infraCache = infraRepository.save(novaInfra);
            log.info(" Infraestrutura criada com ID: {}", infraCache.getIdInfraestrutura());

        } else {
            infraCache.setNome(infraNome);
            infraCache.setUrlEndpoint(publicUrl);
            infraCache.setLatitude(latitudeConfig);
            infraCache.setLongitude(longitudeConfig);
            infraCache.setRaio(raioConfig);
            infraCache.setCapacidade(capacidadeConfig);
            infraCache.setBonusEntrega(bonusEntregaConfig);
            infraCache.setCustoPost(custoPostConfig);
            infraCache.setAtiva(true);
            infraCache = infraRepository.save(infraCache);

            log.info(" Infraestrutura carregada:");
            log.info("   ID: {}", infraCache.getIdInfraestrutura());
            log.info("   Nome: {}", infraCache.getNome());
            log.info("   Ativa: {}", infraCache.isAtiva());
            log.info("   Cobertura: lat={}, lon={}, raio={}m",
                    infraCache.getLatitude(), infraCache.getLongitude(), infraCache.getRaio());
        }

        registarNoUDDI();

        sincronizarSaldos();

        log.info(" Infraestrutura pronta!");
    }

    private void registarNoUDDI() {
        log.info(" [INFRA] Registando no UDDI...");

        try {
            String endpointUrl = publicUrl + "/ws/InfrastructureService";
            uddiRegistrarClient.registar(endpointUrl);
            log.info(" Registado no UDDI com sucesso!");
        } catch (Exception e) {
            log.error(" Erro ao registar no UDDI: {}", e.getMessage());
        }
    }

    @SuppressWarnings("null")
    private void sincronizarSaldos() {
        log.info(" [INFRA] Sincronizando saldos com o AnunciosLoc-Server...");

        try {
            String response = webClientBuilder
                    .baseUrl(anuncioslocUrl)
                    .build()
                    .get()
                    .uri("/api/internal/sincronizacao/saldos")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                log.warn(" Resposta vazia do AnunciosLoc");
                return;
            }

            Map<String, Integer> saldos = objectMapper.readValue(
                    response,
                    new TypeReference<Map<String, Integer>>() {
                    });

            log.info(" Recebidos {} saldos do AnunciosLoc", saldos.size());

            UUID infraId = getInfraId();
            int atualizados = 0;

            for (Map.Entry<String, Integer> entry : saldos.entrySet()) {
                String email = entry.getKey();
                Integer saldo = entry.getValue();

                SaldoUtilizador saldoUser = saldoRepository
                        .findByEmailUtilizadorAndIdInfraestrutura(email, infraId)
                        .orElseGet(() -> {
                            SaldoUtilizador novo = new SaldoUtilizador();
                            novo.setEmailUtilizador(email);
                            novo.setIdInfraestrutura(infraId);
                            novo.setSaldoParcial(0);
                            novo.setPontosGanhos(0);
                            novo.setPontosGastos(0);
                            return novo;
                        });

                saldoUser.setSaldoParcial(saldo);
                saldoUser.setUltimaAtualizacao(LocalDateTime.now());
                saldoRepository.save(saldoUser);
                atualizados++;
            }

            log.info(" Sincronização concluída! {} saldos atualizados.", atualizados);

        } catch (Exception e) {
            log.error(" Erro ao sincronizar saldos: {}", e.getMessage());
        }
    }

    public UUID getInfraId() {
        return infraCache.getIdInfraestrutura();
    }

    public String getInfraNome() {
        return infraCache.getNome();
    }

    public String getPublicUrl() {
        return infraCache.getUrlEndpoint();
    }

    public int getCapacidade() {
        return infraCache.getCapacidade() != null ? infraCache.getCapacidade() : 100;
    }

    public double getLatitude() {
        return infraCache.getLatitude() != null ? infraCache.getLatitude() : latitudeConfig;
    }

    public double getLongitude() {
        return infraCache.getLongitude() != null ? infraCache.getLongitude() : longitudeConfig;
    }

    public double getRaio() {
        return infraCache.getRaio() != null ? infraCache.getRaio() : raioConfig;
    }

    public int getBonusEntrega() {
        return infraCache.getBonusEntrega() != null ? infraCache.getBonusEntrega() : 2;
    }

    public int getCustoPost() {
        return infraCache.getCustoPost() != null ? infraCache.getCustoPost() : 1;
    }

    public boolean isAtiva() {
        return infraCache.isAtiva();
    }

    public Infraestrutura getInfra() {
        return infraCache;
    }

    public int getTotalLocais() {
        return infraCache.getTotalLocais() != null ? infraCache.getTotalLocais() : 0;
    }

    public int getTotalAnuncios() {
        return infraCache.getTotalAnuncios() != null ? infraCache.getTotalAnuncios() : 0;
    }

    public int getTotalEntregas() {
        return infraCache.getTotalEntregas() != null ? infraCache.getTotalEntregas() : 0;
    }

    public int getTotalConexoes() {
        return infraCache.getTotalConexoes() != null ? infraCache.getTotalConexoes() : 0;
    }

    @SuppressWarnings("null")
    @Transactional
    public void incrementarTotalLocais() {
        infraCache.setTotalLocais(getTotalLocais() + 1);
        infraRepository.save(infraCache);
    }

    @SuppressWarnings("null")
    @Transactional
    public void incrementarTotalAnuncios() {
        infraCache.setTotalAnuncios(getTotalAnuncios() + 1);
        infraRepository.save(infraCache);
    }

    @SuppressWarnings("null")
    @Transactional
    public void incrementarTotalEntregas() {
        infraCache.setTotalEntregas(getTotalEntregas() + 1);
        infraRepository.save(infraCache);
    }

    @SuppressWarnings("null")
    @Transactional
    public void incrementarTotalConexoes() {
        infraCache.setTotalConexoes(getTotalConexoes() + 1);
        infraRepository.save(infraCache);
    }

    @SuppressWarnings("null")
    @Transactional
    public void decrementarTotalLocais() {
        int total = getTotalLocais() - 1;
        infraCache.setTotalLocais(total < 0 ? 0 : total);
        infraRepository.save(infraCache);
        log.info("   Total de locais decrementado para: {}", infraCache.getTotalLocais());
    }

    @SuppressWarnings("null")
    @Transactional
    public void decrementarTotalAnuncios(int quantidade) {
        int total = getTotalAnuncios() - quantidade;
        infraCache.setTotalAnuncios(total < 0 ? 0 : total);
        infraRepository.save(infraCache);
        log.info("   Total de anúncios decrementado para: {}", infraCache.getTotalAnuncios());
    }
}
