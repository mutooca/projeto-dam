package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoRequest;
import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoResponse;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiInstanciaResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraestruturaGestaoService {

    private final InfraestruturaRepository infraRepository;
    private final UddiClient uddiClient;

    public List<UddiInstanciaResponse> listarInstanciasUDDI() {
        log.info(" Buscando instâncias no UDDI...");

        List<UddiRecord> registos = uddiClient.descobrirInfraestruturas();
        List<UddiInstanciaResponse> resultado = new ArrayList<>();

        for (UddiRecord record : registos) {
            boolean jaRegistada = infraRepository.findByNome(record.getServiceName()).isPresent();

            String porta = extrairPorta(record.getServiceUrl());

            resultado.add(UddiInstanciaResponse.builder()
                    .serviceName(record.getServiceName())
                    .serviceUrl(record.getServiceUrl())
                    .porta(porta)
                    .status("ONLINE")
                    .jaRegistada(jaRegistada)
                    .build());

            log.info("   Instância: {} | URL: {} | Registada: {}",
                    record.getServiceName(), record.getServiceUrl(), jaRegistada);
        }

        return resultado;
    }

    public Infraestrutura obterInfraestruturaPorNome(String nome) {
        return infraRepository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException(
                        "Infraestrutura '" + nome + "' não encontrada na BD"
                ));
    }

    
    @SuppressWarnings("null")
    public List<Infraestrutura> listarInfraestruturasAtivas() {
        return infraRepository.findAll().stream()
                .filter(Infraestrutura::isAtiva)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Transactional
    @CacheEvict(value = "infraestruturas", allEntries = true)
    public InfraestruturaRegistoResponse registarInfraestrutura(InfraestruturaRegistoRequest request) {
        log.info("🏗️ Registando infraestrutura: {}", request.getNome());

        if (infraRepository.findByNome(request.getNome()).isPresent()) {
            throw new RuntimeException("Infraestrutura com nome '" + request.getNome() + "' já está registada");
        }

        boolean existeNoUDDI = uddiClient.descobrirInfraestruturas().stream()
                .anyMatch(r -> r.getServiceName().equals(request.getNome()));

        if (!existeNoUDDI) {
            throw new RuntimeException("Instância '" + request.getNome() + "' não encontrada no UDDI. " +
                    "Certifique-se que a instância está em execução.");
        }

        Infraestrutura infra = Infraestrutura.builder()
                .nome(request.getNome())
                .url(request.getUrl())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .raio(request.getRaio())
                .capacidade(request.getCapacidade())
                .bonusEntrega(request.getBonusEntrega())
                .custoPost(request.getCustoPost())
                .ativa(true)
                .dataRegisto(LocalDateTime.now())
                .restricoes(request.getRestricoes())
                .build();

        infraRepository.save(infra);

        log.info(" Infraestrutura registada com ID: {}", infra.getId());

        return InfraestruturaRegistoResponse.builder()
                .id(infra.getId())
                .nome(infra.getNome())
                .url(infra.getUrl())
                .latitude(infra.getLatitude())
                .longitude(infra.getLongitude())
                .raio(infra.getRaio())
                .capacidade(infra.getCapacidade())
                .bonusEntrega(infra.getBonusEntrega())
                .custoPost(infra.getCustoPost())
                .ativa(infra.isAtiva())
                .dataRegisto(infra.getDataRegisto())
                .restricoes(infra.getRestricoes())
                .mensagem("Infraestrutura registada com sucesso!")
                .build();
    }

    private String extrairPorta(String url) {
        try {
            // http://localhost:8092 → 8092
            java.net.URI parsedUri = new java.net.URI(url);
            int porta = parsedUri.getPort();
            return porta != -1 ? String.valueOf(porta) : "80";
        } catch (Exception e) {
            return "desconhecida";
        }
    }

    public boolean verificarNomeExistente(String nome) {
        log.info(" Verificando se nome '{}' já existe", nome);
        return infraRepository.findByNome(nome).isPresent();
    }
}