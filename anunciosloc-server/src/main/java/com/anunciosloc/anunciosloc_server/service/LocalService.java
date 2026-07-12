package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.dto.InfraestruturaAtivaDTO;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ListarLocaisResponse;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalService {

    private final InfrastruturaSoapClient soapClient;
    private final InfraestruturaRepository infraestruturaRepository;

    @SuppressWarnings("null")
    public String criarLocal(CriarLocalRequestSOAP request, double latUser, double lonUser) {
        log.info("[ANUNCIOSLOC] Criando local: {}", request.getNome());

        List<InfraProxy> infrasUDDI = soapClient.obterClientes();
        if (infrasUDDI.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível no UDDI");
        }

        log.info("Infraestruturas ativas no UDDI: {}", infrasUDDI.size());

        List<Infraestrutura> infrasBD = infraestruturaRepository.findAll();
        log.info("[MAPAINFRADB] Número de infraestruturas na BD: {}", infrasBD.size());
        infrasBD.forEach(infra -> log.info("   - {} : {}", infra.getNome(), infra.getUrl()));

        Map<String, Infraestrutura> mapaInfrasBD = infrasBD.stream()
                .collect(Collectors.toMap(Infraestrutura::getNome, i -> i));
        log.info("[MAPAINFRADB] dados:",
                mapaInfrasBD);

        List<InfraProxy> infrasRegistadas = infrasUDDI.stream()
                .filter(proxy -> {

                    String nomeInfra = obterNomeInfra(proxy);
                    boolean registada = mapaInfrasBD.containsKey(nomeInfra);
                    if (!registada) {
                        log.warn("Infra '{}' no UDDI mas não registada na BD do AnunciosLoc", nomeInfra);
                    }
                    return registada;
                })
                .collect(Collectors.toList());

        if (infrasRegistadas.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura registada na BD do AnunciosLoc");
        }

        log.info("Infraestruturas registadas na BD: {}", infrasRegistadas.size());

        InfraProxy infraSelecionada = null;
        String nomeSelecionada = null;
        double menorDistancia = Double.MAX_VALUE;

        for (InfraProxy proxy : infrasRegistadas) {
            String nomeInfra = obterNomeInfra(proxy);
            Infraestrutura infraBD = mapaInfrasBD.get(nomeInfra);

            double distancia = HaversineUtil.calcularDistancia(
                    latUser, lonUser,
                    infraBD.getLatitude(),
                    infraBD.getLongitude());

            double raioKm = infraBD.getRaio() / 1000.0;

            log.debug("   Infra {}: distância = {:.2f} km, raio = {:.2f} km",
                    nomeInfra, distancia, raioKm);

            if (distancia <= raioKm && distancia < menorDistancia) {
                menorDistancia = distancia;
                infraSelecionada = proxy;
                nomeSelecionada = nomeInfra;
            }
        }

        if (infraSelecionada == null) {

            log.warn("Nenhuma infra dentro do raio. Usando a mais próxima.");
            infraSelecionada = infrasRegistadas.get(0);
            nomeSelecionada = obterNomeInfra(infraSelecionada);
        }

        log.info("Infra selecionada: {} (distância: {:.2f} km)",
                nomeSelecionada, menorDistancia);

        String resultado = infraSelecionada.criarLocal(request);
        log.info("Local criado na infra: {}", nomeSelecionada);

        return resultado;
    }

    public List<LocalInfoSOAP> listarLocais(double latUser, double lonUser) {
        log.info(" [ANUNCIOSLOC] Listando locais próximos");

        InfraestruturaAtivaDTO infraMaisProxima = encontrarInfraestruturaMaisProxima(latUser, lonUser);

        if (infraMaisProxima == null) {
            log.warn(" Nenhuma infraestrutura ativa e próxima encontrada");
            return List.of();
        }

        log.info("   Infra MAIS PRÓXIMA: {} (distância: {}m)",
                infraMaisProxima.getNome(),
                Math.round(infraMaisProxima.getDistancia()));

        ListarLocaisResponse response = null;
        try {
            response = infraMaisProxima.getProxy().listarLocais(latUser, lonUser);
        } catch (Exception e) {
            log.error("Erro ao listar locais da infra '{}': {}",
                    infraMaisProxima.getNome(), e.getMessage());
            return List.of();
        }

        if (response == null || !response.isSucesso()) {
            String mensagem = response != null ? response.getMensagem() : "Resposta nula";
            log.warn(" Falha ao listar locais: {}", mensagem);
            return List.of();
        }

        List<LocalInfoSOAP> locais = response.getLocais();

        if (locais == null || locais.isEmpty()) {
            log.info(" Nenhum local encontrado na infra '{}'",
                    infraMaisProxima.getNome());
            return List.of();
        }

        log.info(" {} locais encontrados na infra '{}'",
                locais.size(), infraMaisProxima.getNome());

        for (LocalInfoSOAP local : locais) {
            if (local.getDistancia() == null &&
                    local.getLatitude() != null &&
                    local.getLongitude() != null) {
                double distancia = HaversineUtil.calcularDistancia(
                        latUser, lonUser,
                        local.getLatitude(),
                        local.getLongitude());
                local.setDistancia(distancia);
            }
        }

        locais.sort(Comparator.comparingDouble(
                l -> l.getDistancia() != null ? l.getDistancia() : Double.MAX_VALUE));

        if (!locais.isEmpty()) {
            LocalInfoSOAP localMaisProximo = locais.get(0);
            log.info("   Local MAIS PRÓXIMO: {} (ID: {}, distância: {}m)",
                    localMaisProximo.getNome(),
                    localMaisProximo.getIdLocal(),
                    localMaisProximo.getDistancia() != null
                            ? Math.round(localMaisProximo.getDistancia())
                            : "desconhecida");
        }

        return locais;
    }

    /*
     * private static class InfraComDistancia {
     * final InfraProxy proxy;
     * final InfraInfoResponse info;
     * final double distancia;
     * 
     * InfraComDistancia(InfraProxy proxy, InfraInfoResponse info, double distancia)
     * {
     * this.proxy = proxy;
     * this.info = info;
     * this.distancia = distancia;
     * }
     * }
     */

    @SuppressWarnings("null")
    public InfraestruturaAtivaDTO encontrarInfraestruturaMaisProxima(double latUser, double lonUser) {
        log.info(" [LOCAL-SERVICE] Encontrando infraestrutura mais próxima para lat={}, lon={}",
                latUser, lonUser);

        List<InfraProxy> infrasUDDI = soapClient.obterClientes();

        if (infrasUDDI.isEmpty()) {
            log.warn(" Nenhuma infraestrutura encontrada no UDDI");
            return null;
        }

        log.info(" {} infraestruturas encontradas no UDDI", infrasUDDI.size());

        List<Infraestrutura> infrasBD = infraestruturaRepository.findAll();
        Map<String, Infraestrutura> mapaInfrasBD = infrasBD.stream()
                .collect(Collectors.toMap(Infraestrutura::getNome, i -> i));

        List<InfraestruturaAtivaDTO> infrasAtivas = new java.util.ArrayList<>();

        for (InfraProxy proxy : infrasUDDI) {
            try {
                String ping = null;
                try {
                    ping = proxy.ping();
                    log.info("   Ping resposta: '{}'", ping);
                } catch (Exception e) {
                    log.warn("   Ping falhou: {}", e.getMessage());
                    continue;
                }

                if (ping == null || !ping.startsWith("PONG")) {
                    log.warn("    Infra NÃO responde ao ping: {}", proxy.getServiceUrl());
                    continue;
                }

                log.info("    Ping OK para: {}", proxy.getServiceUrl());

                String nomeInfra = obterNomeInfra(proxy);
                log.info("   Nome da infra: {}", nomeInfra);

                if (nomeInfra == null) {
                    log.warn("   Não foi possível obter nome da infra: {}", proxy.getServiceUrl());
                    continue;
                }

                Infraestrutura infraBD = mapaInfrasBD.get(nomeInfra);
                if (infraBD == null) {
                    log.warn("    Infra '{}' está no UDDI mas NÃO registada na BD", nomeInfra);
                    continue;
                }

                if (!infraBD.isAtiva()) {
                    log.warn("    Infra '{}' está INATIVA na BD", nomeInfra);
                    continue;
                }

                double distancia = HaversineUtil.calcularDistancia(
                        latUser, lonUser,
                        infraBD.getLatitude(),
                        infraBD.getLongitude());

                if (distancia > infraBD.getRaio()) {
                    log.warn("    User está fora do raio da infra '{}' ({}m > {}m)",
                            nomeInfra, Math.round(distancia), infraBD.getRaio());
                    continue;
                }

                InfraestruturaAtivaDTO infraAtiva = InfraestruturaAtivaDTO.builder()
                        .nome(nomeInfra)
                        .url(infraBD.getUrl())
                        .latitude(infraBD.getLatitude())
                        .longitude(infraBD.getLongitude())
                        .raio(infraBD.getRaio())
                        .distancia(distancia)
                        .proxy(proxy)
                        .ativa(true)
                        .build();

                infrasAtivas.add(infraAtiva);

            } catch (Exception e) {
                log.error("Erro ao processar infra: {}", e.getMessage(), e);
            }
        }

        if (infrasAtivas.isEmpty()) {
            log.warn(" Nenhuma infraestrutura ativa e próxima encontrada");
            return null;
        }

        infrasAtivas.sort(Comparator.comparingDouble(InfraestruturaAtivaDTO::getDistancia));

        InfraestruturaAtivaDTO maisProxima = infrasAtivas.get(0);
        log.info("  Infra MAIS PRÓXIMA: {} (distância: {}m)",
                maisProxima.getNome(), Math.round(maisProxima.getDistancia()));

        return maisProxima;
    }

    public boolean isUserPertoDeLocal(String email, double latUser, double lonUser) {
        List<LocalInfoSOAP> locais = listarLocais(latUser, lonUser);

        if (locais.isEmpty()) {
            return false;
        }

        LocalInfoSOAP localMaisProximo = locais.get(0);

        if (localMaisProximo.getDistancia() != null &&
                localMaisProximo.getDistancia() <= 50.0) {
            log.info("  User está perto do local '{}' ({}m)",
                    localMaisProximo.getNome(),
                    Math.round(localMaisProximo.getDistancia()));
            return true;
        }

        log.info("  User está longe do local mais próximo ({}m)",
                localMaisProximo.getDistancia() != null
                        ? Math.round(localMaisProximo.getDistancia())
                        : "desconhecida");
        return false;
    }

    private String obterNomeInfra(InfraProxy proxy) {
        try {
            String url = proxy.getServiceUrl();
            log.info(" URL do proxy: {}", url);

            String urlLimpa = url.replace("/ws/InfrastructureService", "");
            log.info(" URL limpa para busca: {}", urlLimpa);

            Optional<Infraestrutura> infraOpt = infraestruturaRepository.findByUrl(urlLimpa);

            if (infraOpt.isPresent()) {
                String nome = infraOpt.get().getNome();
                log.info(" Nome encontrado na BD: {}", nome);
                return nome;
            }

            try {
                InfraInfoResponse info = proxy.obterInfoInfraestrutura();
                if (info != null && info.getNome() != null && !info.getNome().isEmpty()) {
                    log.info(" Nome obtido via SOAP: {}", info.getNome());
                    return info.getNome();
                }
            } catch (Exception e) {
                log.warn("SOAP falhou: {}", e.getMessage());
            }

            log.warn(" URL '{}' não encontrada na BD nem via SOAP", urlLimpa);
            return null;

        } catch (Exception e) {
            log.warn("Erro ao obter nome da infra: {}", e.getMessage());
            return null;
        }
    }

}