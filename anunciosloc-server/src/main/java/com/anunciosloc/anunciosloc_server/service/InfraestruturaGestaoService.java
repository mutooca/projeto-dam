package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoRequest;
import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoResponse;
import com.anunciosloc.anunciosloc_server.dto.RecolocarInfraRequest;
import com.anunciosloc.anunciosloc_server.dto.RedimensionarInfraRequest;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiInstanciaResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiRecord;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraestruturaGestaoService {

    private final InfraestruturaRepository infraRepository;
    private final InfrastruturaSoapClient soapClient;
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
                        "Infraestrutura '" + nome + "' não encontrada na BD"));
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

    @SuppressWarnings("null")
    public String redimensionarInfraestrutura(UUID id, RedimensionarInfraRequest request) {
        log.info(" [ANUNCIOSLOC] Redimensionando infraestrutura: {}", id);

        Infraestrutura infra = infraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        log.info("   Infra encontrada: {}", infra.getNome());

        if (!infra.isAtiva()) {
            throw new RuntimeException("Infraestrutura está INATIVA");
        }

        if (request.getCapacidade() != null) {
            infra.setCapacidade(request.getCapacidade());
            log.info("   Capacidade atualizada para: {}", request.getCapacidade());
        }
        if (request.getBonusEntrega() != null) {
            infra.setBonusEntrega(request.getBonusEntrega());
            log.info("   Bónus entrega atualizado para: {}", request.getBonusEntrega());
        }
        if (request.getCustoPost() != null) {
            infra.setCustoPost(request.getCustoPost());
            log.info("   Custo post atualizado para: {}", request.getCustoPost());
        }
        if (request.getRaio() != null) {
            infra.setRaio(request.getRaio());
            log.info("   Raio atualizado para: {}m", request.getRaio());
        }

        infraRepository.save(infra);

        log.info("  Infraestrutura redimensionada com sucesso!");
        return "Infraestrutura redimensionada com sucesso!";
    }

    @Transactional
    public String recolocarInfraestrutura(UUID id, RecolocarInfraRequest request) {
        log.info("═══════════════════════════════════════════════════════════════");
        log.info(" [ADMIN] Recolocando infraestrutura: {}", id);
        log.info("   Nova latitude: {}", request.getLatitude());
        log.info("   Nova longitude: {}", request.getLongitude());
        log.info("   Novo raio: {}m", request.getRaio());
        log.info("═══════════════════════════════════════════════════════════════");

        try {
            
            Infraestrutura infra = infraRepository.findByIdWithLock(id)
                    .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

            log.info("   Infra encontrada: {}", infra.getNome());
            log.info("   Versão atual: {}", infra.getVersao());

           
            if (!infra.isAtiva()) {
                throw new RuntimeException("Infraestrutura está INATIVA. Ative antes de recolocar.");
            }

    
            boolean houveMudanca = false;
            
            if (!infra.getLatitude().equals(request.getLatitude())) {
                houveMudanca = true;
                log.info("   Latitude: {} → {}", infra.getLatitude(), request.getLatitude());
            }
            if (!infra.getLongitude().equals(request.getLongitude())) {
                houveMudanca = true;
                log.info("   Longitude: {} → {}", infra.getLongitude(), request.getLongitude());
            }
            if (!infra.getRaio().equals(request.getRaio())) {
                houveMudanca = true;
                log.info("   Raio: {}m → {}m", infra.getRaio(), request.getRaio());
            }

            if (!houveMudanca) {
                log.info("  Nenhuma mudança detectada");
                return "Nenhuma alteração realizada (coordenadas já estão atuais)";
            }

            validarConflitoDeCoordenadas(infra, request);

            boolean estaOnline = verificarInfraOnline(infra.getNome());
            if (!estaOnline) {
                log.warn("    ATENÇÃO: Infraestrutura não está ONLINE no momento!");
                log.warn("   A recolocação será feita mesmo assim, mas os utilizadores");
                log.warn("   só verão as mudanças quando a infra voltar a ficar online.");
            }

            
            infra.setLatitude(request.getLatitude());
            infra.setLongitude(request.getLongitude());
            infra.setRaio(request.getRaio());
            
            // A versao eh incrementada automaticamente pelo @Version
            Infraestrutura saved = infraRepository.save(infra);

            log.info("  Infraestrutura recolocada com sucesso!");
            log.info("   Nova versão: {}", saved.getVersao());
            log.info("═══════════════════════════════════════════════════════════════");

            return String.format(
                "Infraestrutura recolocada com sucesso! " +
                "Novas coordenadas: (%.6f, %.6f), Raio: %.0fm, Versão: %d",
                saved.getLatitude(), saved.getLongitude(), 
                saved.getRaio(), saved.getVersao()
            );

        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            log.error(" CONFLITO DE CONCORRÊNCIA: {}", e.getMessage());
            throw new RuntimeException(
                "A infraestrutura foi modificada por outro administrador. " +
                "Por favor, recarregue e tente novamente."
            );
        } catch (Exception e) {
            log.error("  Erro ao recolocar infra: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao recolocar infraestrutura: " + e.getMessage());
        }
    }

    
    private void validarConflitoDeCoordenadas(Infraestrutura infraAtual, RecolocarInfraRequest request) {
        log.info("   [VALIDAÇÃO] Verificando conflitos com outras infraestruturas...");

        List<Infraestrutura> todasInfras = infraRepository.findAll();

        for (Infraestrutura outra : todasInfras) {
            
            if (outra.getId().equals(infraAtual.getId())) continue;

            
            double distancia = HaversineUtil.calcularDistancia(
                request.getLatitude(), request.getLongitude(),
                outra.getLatitude(), outra.getLongitude()
            );

            // Verificar se estão muito próximas (menos de 100m)
            if (distancia < 100.0) {
                log.warn("  Conflito detectado com '{}' (distância: {:.0f}m)", 
                        outra.getNome(), distancia);
                throw new RuntimeException(
                    String.format(
                        "Infraestrutura '%s' já está muito próxima (%.0fm). " +
                        "Distância mínima permitida: 100m",
                        outra.getNome(), distancia
                    )
                );
            }

            // Verificar sobreposicao significativa de raios
            double somaRaio = request.getRaio() + outra.getRaio();
            if (distancia < somaRaio * 0.7) { // 70% de sobreposicao
                log.warn("  Atenção: Áreas de cobertura sobrepõem-se com '{}' " +
                        "(distância: {:.0f}m, raios: {:.0f}m + {:.0f}m)", 
                        outra.getNome(), distancia, request.getRaio(), outra.getRaio());
                
            }
        }

        log.info("  Nenhum conflito crítico detectado");
    }

    
    private boolean verificarInfraOnline(String nomeInfra) {
        log.info("   [VALIDAÇÃO] Verificando se '{}' está online...", nomeInfra);

        try {
            List<InfraProxy> infras = soapClient.obterClientes();
            
            for (InfraProxy proxy : infras) {
                try {
                    String ping = proxy.ping();
                    if (ping != null && ping.startsWith("PONG")) {
                        log.info("  '{}' está ONLINE", nomeInfra);
                        return true;
                    }
                } catch (Exception e) {
                    
                }
            }
            
            log.warn("  '{}' NÃO está online", nomeInfra);
            return false;
            
        } catch (Exception e) {
            log.warn("  Erro ao verificar online: {}", e.getMessage());
            return false;
        }
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