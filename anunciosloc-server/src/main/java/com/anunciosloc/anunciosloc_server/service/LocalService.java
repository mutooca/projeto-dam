package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.CriarLocalRequest;
import com.anunciosloc.anunciosloc_server.dto.CriarLocalResponse;
import com.anunciosloc.anunciosloc_server.dto.EditarLocalRequest;
import com.anunciosloc.anunciosloc_server.dto.EditarLocalResponse;
import com.anunciosloc.anunciosloc_server.dto.InfraestruturaAtivaDTO;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.EditarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.EditarLocalResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ListarLocaisResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public CriarLocalResponse criarLocal(CriarLocalRequest request, double latUser, double lonUser) {
        log.info("═══════════════════════════════════════════════════════════════");
        log.info("[ANUNCIOSLOC] Criando local: {}", request.getNome());
        log.info("   Utilizador: {}", request.getEmailUtilizador());
        log.info("   GPS local: lat={}, lon={}, raio={}m",
                request.getLatitude(), request.getLongitude(), request.getRaio());
        log.info("   GPS utilizador: lat={}, lon={}", latUser, lonUser);
        log.info("═══════════════════════════════════════════════════════════════");

        CriarLocalRequestSOAP soapRequest = CriarLocalRequestSOAP.builder()
                .nome(request.getNome())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .raio(request.getRaio())
                .latUtilizador(latUser)
                .lonUtilizador(lonUser)
                .emailUtilizador(request.getEmailUtilizador())
                .ssidWifi(request.getSsidWifi())
                .build();

        List<InfraestruturaAtivaDTO> infraestruturasDisponiveis = resolverInfraestruturasDisponiveis();
        if (infraestruturasDisponiveis.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível no UDDI");
        }

        InfraProxy infraSelecionada = null;
        String nomeSelecionada = null;
        double menorDistancia = Double.MAX_VALUE;

        for (InfraestruturaAtivaDTO infra : infraestruturasDisponiveis) {
            double distancia = HaversineUtil.calcularDistancia(
                    latUser, lonUser,
                    infra.getLatitude(),
                    infra.getLongitude());

            double raioMetros = infra.getRaio();

            log.info("   Infra {}: distância={}m, raio={}m, url={}",
                    infra.getNome(),
                    Math.round(distancia),
                    Math.round(raioMetros),
                    infra.getUrl());

            if (distancia <= raioMetros && distancia < menorDistancia) {
                menorDistancia = distancia;
                infraSelecionada = infra.getProxy();
                nomeSelecionada = infra.getNome();
            }
        }

        if (infraSelecionada == null) {
            infraestruturasDisponiveis.sort(Comparator.comparingDouble(infra ->
                    HaversineUtil.calcularDistancia(
                            latUser, lonUser, infra.getLatitude(), infra.getLongitude())));
            InfraestruturaAtivaDTO maisProxima = infraestruturasDisponiveis.get(0);
            double distancia = HaversineUtil.calcularDistancia(
                    latUser, lonUser, maisProxima.getLatitude(), maisProxima.getLongitude());

            log.warn("Nenhuma infra dentro do raio. A usar a mais próxima: {} ({}m)",
                    maisProxima.getNome(),
                    Math.round(distancia));
            infraSelecionada = maisProxima.getProxy();
            nomeSelecionada = maisProxima.getNome();
            menorDistancia = distancia;
        }

        log.info("Infra selecionada: {} (distância: {}m)",
                nomeSelecionada, Math.round(menorDistancia));

        CriarLocalResponseSOAP resultado = infraSelecionada.criarLocal(soapRequest);
        if (resultado == null) {
            throw new RuntimeException("Resposta nula ao criar local");
        }

        if (!resultado.isSucesso()) {
            throw new RuntimeException(
                    resultado.getMensagem() != null && !resultado.getMensagem().isBlank()
                            ? resultado.getMensagem()
                            : "Falha ao criar local");
        }

        log.info("Local criado na infra: {} -> idLocal={}, mensagem={}",
                nomeSelecionada, resultado.getIdLocal(), resultado.getMensagem());

        return CriarLocalResponse.builder()
                .idLocal(resultado.getIdLocal())
                .nome(request.getNome())
                .latitude(resultado.getLatitude() != 0.0 ? resultado.getLatitude() : request.getLatitude())
                .longitude(resultado.getLongitude() != 0.0 ? resultado.getLongitude() : request.getLongitude())
                .raio(resultado.getRaio() != 0.0 ? resultado.getRaio() : request.getRaio())
                .sucesso(true)
                .mensagem(resultado.getMensagem())
                .build();
    }

    public String eliminarLocal(String idLocal, String emailUtilizador) {
        log.info(" [ANUNCIOSLOC] Eliminando local: {} por {}", idLocal, emailUtilizador);

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        
        for (InfraProxy infra : infras) {
            try {
                MensagemResponse response = infra.eliminarLocal(idLocal, emailUtilizador);
                if (response != null && response.isSucesso()) {
                    log.info(" Local eliminado na infra: {}", infra.getServiceUrl());
                    return response.getMensagem();
                }
            } catch (Exception e) {
                log.warn(" Falha ao eliminar local na infra {}: {}",
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível eliminar o local");
    }

    public EditarLocalResponse editarLocal(String idLocal, EditarLocalRequest request, String emailUtilizador) {
        log.info(" [ANUNCIOSLOC] Editando local: {} por {} (assumido: qualquer utilizador autenticado pode editar)",
                idLocal, emailUtilizador);
        log.info("   Alterações pedidas: nome={}, lat={}, lon={}, raio={}, ssidWifi={}",
                request.getNome(), request.getLatitude(), request.getLongitude(),
                request.getRaio(), request.getSsidWifi());

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        EditarLocalRequestSOAP soapRequest = EditarLocalRequestSOAP.builder()
                .idLocal(idLocal)
                .nome(request.getNome())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .raio(request.getRaio())
                .ssidWifi(request.getSsidWifi())
                .emailUtilizador(emailUtilizador)
                .build();

        for (InfraProxy infra : infras) {
            try {
                EditarLocalResponseSOAP response = infra.editarLocal(soapRequest);
                if (response != null && response.isSucesso()) {
                    log.info(" Local editado na infra: {}", infra.getServiceUrl());
                    return EditarLocalResponse.builder()
                            .idLocal(response.getIdLocal())
                            .nome(response.getNome())
                            .latitude(response.getLatitude())
                            .longitude(response.getLongitude())
                            .raio(response.getRaio())
                            .ssidWifi(response.getSsidWifi())
                            .sucesso(true)
                            .mensagem(response.getMensagem())
                            .build();
                }
            } catch (Exception e) {
                log.warn(" Falha ao editar local na infra {}: {}",
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível editar o local");
    }

    public List<LocalInfoSOAP> listarLocais(double latUser, double lonUser) {
        log.info(" [ANUNCIOSLOC] Listando locais próximos para lat={}, lon={}", latUser, lonUser);

        List<InfraestruturaAtivaDTO> infraestruturas = resolverInfraestruturasDisponiveis();
        if (infraestruturas.isEmpty()) {
            log.warn(" Nenhuma infraestrutura disponível para listar locais");
            return List.of();
        }

        List<LocalInfoSOAP> locaisAgregados = new ArrayList<>();

        for (InfraestruturaAtivaDTO infra : infraestruturas) {
            double distanciaInfra = HaversineUtil.calcularDistancia(
                    latUser,
                    lonUser,
                    infra.getLatitude(),
                    infra.getLongitude());

            infra.setDistancia(distanciaInfra);
            log.info("   Consultando infra '{}' (distância={}m)",
                    infra.getNome(),
                    Math.round(distanciaInfra));

            try {
                ListarLocaisResponse response = infra.getProxy().listarLocais(latUser, lonUser);
                if (response == null) {
                    log.warn("   Infra '{}' devolveu resposta nula", infra.getNome());
                    continue;
                }

                if (!response.isSucesso()) {
                    log.warn("   Infra '{}' devolveu sucesso=false: {}",
                            infra.getNome(),
                            response.getMensagem());
                    continue;
                }

                List<LocalInfoSOAP> locaisInfra = response.getLocais();
                if (locaisInfra == null || locaisInfra.isEmpty()) {
                    log.info("   Infra '{}' não devolveu locais próximos", infra.getNome());
                    continue;
                }

                for (LocalInfoSOAP local : locaisInfra) {
                    if (local.getDistancia() == null
                            && local.getLatitude() != null
                            && local.getLongitude() != null) {
                        double distancia = HaversineUtil.calcularDistancia(
                                latUser,
                                lonUser,
                                local.getLatitude(),
                                local.getLongitude());
                        local.setDistancia(distancia);
                    }
                    locaisAgregados.add(local);
                }

                log.info("   Infra '{}' devolveu {} locais",
                        infra.getNome(),
                        locaisInfra.size());
            } catch (Exception e) {
                log.error("Erro ao listar locais da infra '{}': {}",
                        infra.getNome(), e.getMessage(), e);
            }
        }

        if (locaisAgregados.isEmpty()) {
            log.info(" Nenhum local próximo encontrado em nenhuma infraestrutura");
            return List.of();
        }

        Map<String, LocalInfoSOAP> locaisPorId = locaisAgregados.stream()
                .filter(local -> local.getIdLocal() != null && !local.getIdLocal().isBlank())
                .collect(Collectors.toMap(
                        LocalInfoSOAP::getIdLocal,
                        local -> local,
                        (atual, novo) -> {
                            double distanciaAtual = atual.getDistancia() != null
                                    ? atual.getDistancia()
                                    : Double.MAX_VALUE;
                            double distanciaNova = novo.getDistancia() != null
                                    ? novo.getDistancia()
                                    : Double.MAX_VALUE;
                            return distanciaNova < distanciaAtual ? novo : atual;
                        }));

        List<LocalInfoSOAP> locaisOrdenados = new ArrayList<>(locaisPorId.values());
        locaisOrdenados.sort(Comparator.comparingDouble(
                local -> local.getDistancia() != null ? local.getDistancia() : Double.MAX_VALUE));

        LocalInfoSOAP maisProximo = locaisOrdenados.get(0);
        log.info(" {} locais agregados. Mais próximo: {} (ID: {}, distância: {}m)",
                locaisOrdenados.size(),
                maisProximo.getNome(),
                maisProximo.getIdLocal(),
                maisProximo.getDistancia() != null
                        ? Math.round(maisProximo.getDistancia())
                        : "desconhecida");

        // A infra devolve todos os locais (o raio próprio de cada local é o geofence de entrega,
        // não o critério de "está perto"), por isso é aqui, num único sítio, que se decide o que
        // conta como "próximo" para efeitos de listagem — o mesmo limiar usado para elegibilidade
        // de anúncios (AnuncioController), evitando os dois critérios divergirem.
        List<LocalInfoSOAP> locaisProximos = locaisOrdenados.stream()
                .filter(local -> local.getDistancia() != null && local.getDistancia() <= 100.0)
                .collect(Collectors.toList());

        log.info(" {} local(is) dentro do alcance (<=100m) de {} candidato(s) agregado(s)",
                locaisProximos.size(), locaisOrdenados.size());

        return locaisProximos;
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

        List<InfraestruturaAtivaDTO> infrasAtivas = resolverInfraestruturasDisponiveis();
        if (infrasAtivas.isEmpty()) {
            log.warn(" Nenhuma infraestrutura encontrada no UDDI");
            return null;
        }

        for (InfraestruturaAtivaDTO infra : infrasAtivas) {
            double distancia = HaversineUtil.calcularDistancia(
                    latUser, lonUser,
                    infra.getLatitude(),
                    infra.getLongitude());

            infra.setDistancia(distancia);
        }

        infrasAtivas.sort(Comparator.comparingDouble(InfraestruturaAtivaDTO::getDistancia));

        InfraestruturaAtivaDTO maisProxima = infrasAtivas.get(0);
        log.info("  Infra MAIS PRÓXIMA: {} (distância: {}m)",
                maisProxima.getNome(), Math.round(maisProxima.getDistancia()));

        return maisProxima;
    }

    private List<InfraestruturaAtivaDTO> resolverInfraestruturasDisponiveis() {
        List<InfraProxy> infrasUDDI = soapClient.obterClientes();
        if (infrasUDDI.isEmpty()) {
            log.warn(" Nenhuma infraestrutura encontrada no UDDI");
            return List.of();
        }

        log.info(" {} infraestruturas encontradas no UDDI", infrasUDDI.size());

        List<InfraestruturaAtivaDTO> resultado = new ArrayList<>();
        for (InfraProxy proxy : infrasUDDI) {
            try {
                String ping = proxy.ping();
                log.info("   Ping infra {} -> {}", proxy.getServiceUrl(), ping);
                if (ping == null || !ping.startsWith("PONG")) {
                    log.warn("   Infra não respondeu corretamente ao ping: {}", proxy.getServiceUrl());
                    continue;
                }

                InfraInfoResponse info = proxy.obterInfoInfraestrutura();
                if (info == null) {
                    log.warn("   Infra '{}' devolveu info nula", proxy.getServiceUrl());
                    continue;
                }

                if (!info.isSucesso()) {
                    log.warn("   Infra '{}' devolveu sucesso=false: {}",
                            proxy.getServiceUrl(), info.getMensagem());
                    continue;
                }

                Infraestrutura infra = sincronizarInfraestrutura(info, proxy);
                if (infra == null) {
                    continue;
                }

                if (!infra.isAtiva()) {
                    log.warn("   Infra '{}' está marcada como inativa", infra.getNome());
                    continue;
                }

                resultado.add(InfraestruturaAtivaDTO.builder()
                        .nome(infra.getNome())
                        .url(infra.getUrl())
                        .latitude(infra.getLatitude())
                        .longitude(infra.getLongitude())
                        .raio(infra.getRaio())
                        .proxy(proxy)
                        .ativa(true)
                        .build());
            } catch (Exception e) {
                log.error("Erro ao resolver infra '{}': {}", proxy.getServiceUrl(), e.getMessage(), e);
            }
        }

        return resultado;
    }

    private Infraestrutura sincronizarInfraestrutura(InfraInfoResponse info, InfraProxy proxy) {
        if (info.getNome() == null || info.getNome().isBlank()) {
            log.warn("   Infra sem nome válido para proxy {}", proxy.getServiceUrl());
            return null;
        }

        String baseUrl = normalizarBaseUrl(info.getUrl() != null && !info.getUrl().isBlank()
                ? info.getUrl()
                : proxy.getServiceUrl());

        Infraestrutura infra = infraestruturaRepository.findByNome(info.getNome())
                .orElseGet(() -> infraestruturaRepository.findByUrl(baseUrl).orElse(null));

        boolean novaInfra = infra == null;
        if (infra == null) {
            infra = new Infraestrutura();
            infra.setDataRegisto(LocalDateTime.now());
            infra.setAtiva(true);
        }

        Double latitude = info.getLatitude() != null ? info.getLatitude() : infra.getLatitude();
        Double longitude = info.getLongitude() != null ? info.getLongitude() : infra.getLongitude();
        Double raio = info.getRaio() != null ? info.getRaio() : infra.getRaio();

        if (latitude == null || longitude == null || raio == null) {
            log.warn("   Infra '{}' sem coordenadas completas. lat={}, lon={}, raio={}",
                    info.getNome(), latitude, longitude, raio);
            return null;
        }

        infra.setNome(info.getNome());
        infra.setUrl(baseUrl);
        infra.setLatitude(latitude);
        infra.setLongitude(longitude);
        infra.setRaio(raio);
        infra.setCapacidade(info.getCapacidade() != null ? info.getCapacidade() : 100);
        infra.setBonusEntrega(info.getBonusEntrega() != null ? info.getBonusEntrega() : 0);
        infra.setCustoPost(info.getCustoPost() != null ? info.getCustoPost() : 0);
        infra.setAtiva(info.isAtiva());

        Infraestrutura guardada = infraestruturaRepository.save(infra);
        if (novaInfra) {
            log.info("   Infra sincronizada automaticamente na BD: {} -> {}",
                    guardada.getNome(), guardada.getUrl());
        } else {
            log.info("   Infra atualizada na BD: {} -> {}", guardada.getNome(), guardada.getUrl());
        }
        return guardada;
    }

    private String normalizarBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }

        String normalizada = url.replace("/ws/InfrastructureService", "");
        if (normalizada.endsWith("/")) {
            return normalizada.substring(0, normalizada.length() - 1);
        }
        return normalizada;
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
