package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiRecord;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraDisponivelUddiResponse;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfraestruturaService {

    private final InfraestruturaRepository infraRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final CoordenadaGpsRepository gpsRepository;
    private final CoordenadaWifiRepository wifiRepository;
    private final RegistoEstatisticoRepository estatisticoRepository;
    private final InfrastruturaSoapClient soapClient;
    private final InfraDisponibilidadeService infraDisponibilidade;
    private final UddiClient uddiClient;

    
    @Transactional
    @CacheEvict(value = "infraestruturas", allEntries = true)
    public Infraestrutura registarInfraestrutura(RegistarInfraRequest request) {
        Utilizador gestor = utilizadorRepository.findByEmail(request.getEmailGestor())
            .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        if (!"ADMIN".equals(gestor.getRole())) {
            throw new RuntimeException("Apenas gestores podem criar infraestruturas");
        }

        List<UddiRecord> registadosUddi = 
            uddiClient.descobrirInfraestruturas();

        boolean nomeExisteNoUddi = registadosUddi.stream()
                .anyMatch(r -> r.getServiceName().equals(request.getNome()));

        if (!nomeExisteNoUddi) {
            // Sugere os que estão disponíveis
            List<String> disponiveis = registadosUddi.stream()
                    .map(UddiRecord::getServiceName)
                    .filter(nome -> infraRepository.findByNome(nome).isEmpty())
                    .toList();

            String sugestao = disponiveis.isEmpty()
                    ? "Nenhum servidor disponível no UDDI."
                    : "Servidores disponíveis: " + String.join(", ", disponiveis);

            throw new RuntimeException(
                "O nome '" + request.getNome() + "' não está registado no UDDI. " +
                sugestao);
        }

        if (infraRepository.findByNome(request.getNome()).isPresent()) {
            
            List<String> outrosDisponiveis = registadosUddi.stream()
                    .map(UddiRecord::getServiceName)
                    .filter(nome -> infraRepository.findByNome(nome).isEmpty())
                    .toList();

            String sugestao = outrosDisponiveis.isEmpty()
                    ? "Todos os servidores já foram associados."
                    : "Outros disponíveis: " + String.join(", ", outrosDisponiveis);

            throw new RuntimeException(
                "O servidor '" + request.getNome() + 
                "' já tem coordenadas associadas. " + sugestao);
        }

        
        Infraestrutura infra = new Infraestrutura();
        infra.setNome(request.getNome());
        infra.setCapacidade(request.getCapacidade());
        infra.setBonusEntrega(request.getBonusEntrega());
        infra.setCustoPost(request.getCustoPost());
        infra.setDataRegisto(LocalDateTime.now());
        infra.setAtiva(true);
        infra.setGestor(gestor);
        infra.setTotalAnuncios(0);
        infra.setTotalEntregas(0);
        infra.setTotalConexoes(0);

        
        CoordenadaGps gps = new CoordenadaGps();
        gps.setLatitude(request.getLatitude());
        gps.setLongitude(request.getLongitude());
        gps.setRaio(request.getRaio());
        CoordenadaGps savedGps = gpsRepository.save(gps);

        
        CoordenadaWifi wifi = null;
        if (request.getSsidWifi() != null && !request.getSsidWifi().isEmpty()) {
            wifi = new CoordenadaWifi();
            wifi.setSsid(request.getSsidWifi());
            wifi = wifiRepository.save(wifi);
        }

        
        Local local = new Local();
        local.setNome("Local principal");
        local.setInfraestrutura(infra);
        local.setCoordenadaGps(savedGps);
        local.setCoordenadaWifi(wifi);

        infra.getLocais().add(local);

        
        RegistoEstatistico estatistico = new RegistoEstatistico();
        estatistico.setInfraestrutura(infra);
        estatistico.setDataRegisto(LocalDate.now()); 
        estatistico.setTotalAnuncio(0);
        estatistico.setTotalEntrega(0);
        estatistico.setTotalConexao(0);
        estatisticoRepository.save(estatistico);
        infra.setRegistoEstatistico(estatistico);

        return infraRepository.save(infra);
    }

    @Cacheable(value = "infraestruturas", key = "#id")
    public InfraestruturaResponse obterInfoInfraestrutura(@NonNull UUID id) {
        Infraestrutura infra = infraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        return InfraestruturaResponse.builder()
            .id(infra.getIdInfraestrutura())
            .nome(infra.getNome())
            .capacidade(infra.getCapacidade())
            .bonusEntrega(infra.getBonusEntrega())
            .custoPost(infra.getCustoPost())
            .totalAnuncios(infra.getTotalAnuncios())
            .totalEntregas(infra.getTotalEntregas())
            .conexoesAtuais(infra.getTotalConexoes())
            .coordenadas(infra.getLocais().stream()
                .map(local -> {
                    CoordenadaResponse.CoordenadaResponseBuilder builder = CoordenadaResponse.builder();
                    if (local.getCoordenadaGps() != null) {
                        builder.latitude(local.getCoordenadaGps().getLatitude())
                               .longitude(local.getCoordenadaGps().getLongitude())
                               .raio(local.getCoordenadaGps().getRaio());
                    }
                    if (local.getCoordenadaWifi() != null) {
                        builder.ssidWifi(local.getCoordenadaWifi().getSsid());
                    }
                    return builder.build();
                })
                .collect(Collectors.toList()))
            .build();
    }


    @Cacheable(value = "infraestruturas", key = "#lat + ',' + #lon + ',' + #k")
    public List<InfraestruturaResponse> listarInfraestruturasProximas(
            double lat, double lon, int k) {

        List<Infraestrutura> todas = infraRepository.findByAtivaTrue();

        return todas.stream()
            .map(infra -> {

                // Distância mínima a qualquer local GPS da infraestrutura
                double distanciaMetros = infra.getLocais().stream()
                    .filter(l -> l.getCoordenadaGps() != null)
                    .mapToDouble(l -> HaversineUtil.calcularDistancia(
                        lat, lon,
                        l.getCoordenadaGps().getLatitude(),
                        l.getCoordenadaGps().getLongitude()))
                    .min()
                    .orElse(Double.MAX_VALUE);

                
                int conexoesDisponiveis = infra.getCapacidade(); 
                if (infraDisponibilidade.estaDisponivel(infra.getNome())) {
                    try {
                        InfraProxy proxy = soapClient.obterClientePorNome(infra.getNome());
                        conexoesDisponiveis = proxy.obterInfoInfraestrutura()
                                                .getConexoesDisponiveis();
                    } catch (Exception e) {
                        log.warn("Fallback para capacidade total: {}", e.getMessage());
                    }
                }
                
                return InfraestruturaResponse.builder()
                    .id(infra.getIdInfraestrutura())
                    .nome(infra.getNome() != null ? infra.getNome() : "Sem nome")
                    .distanciaKm(distanciaMetros / 1000.0) 
                    .capacidade(infra.getCapacidade())
                    .bonusEntrega(infra.getBonusEntrega())
                    .custoPost(infra.getCustoPost())
                    .conexoesAtuais(conexoesDisponiveis)
                    .build();
            })
            .filter(r -> r.getDistanciaKm() < Double.MAX_VALUE)
            .sorted(Comparator.comparingDouble(InfraestruturaResponse::getDistanciaKm))
            .limit(k)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Cacheable(value = "locais", key = "#infraId + ',' + #lat + ',' + #lon")
    public List<LocalResponse> listarLocais(UUID infraId, double lat, double lon) {

        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        // Verifica se o utilizador está dentro do raio de pelo menos um local da infra
        boolean dentroDeUmLocal = infra.getLocais().stream()
            .filter(l -> l.getCoordenadaGps() != null)
            .anyMatch(l -> HaversineUtil.calcularDistancia(
                    lat, lon,
                    l.getCoordenadaGps().getLatitude(),
                    l.getCoordenadaGps().getLongitude())
                <= l.getCoordenadaGps().getRaio());

        if (!dentroDeUmLocal) {
            throw new RuntimeException(
                "Não está dentro do raio de nenhum local desta infraestrutura.");
        }

        // Devolve todos os locais da infra
        return infra.getLocais().stream()
                .filter(l -> l.getCoordenadaGps() != null)
                .map(local -> LocalResponse.builder()
                        .idLocal(local.getIdLocal().toString())
                        .nome(local.getNome())
                        .latitude(local.getCoordenadaGps().getLatitude())
                        .longitude(local.getCoordenadaGps().getLongitude())
                        .raio(local.getCoordenadaGps().getRaio())
                        .build())
                .toList();
    }

    @Cacheable(value = "infraestruturas", key = "'uddi-disponiveis'")
    public List<InfraDisponivelUddiResponse> listarDisponiveisUddi() {
    List<UddiRecord> registados = uddiClient.descobrirInfraestruturas();

    List<String> nomesNaBd = infraRepository.findAll()
            .stream()
            .map(Infraestrutura::getNome)
            .toList();

    return registados.stream()
            .map(r -> InfraDisponivelUddiResponse.builder()
                    .nome(r.getServiceName())
                    .url(r.getServiceUrl())
                    .registadoNaBd(nomesNaBd.contains(r.getServiceName()))
                    .build())
            .toList();
    }

        /***newssss */
        @SuppressWarnings("null")
        @Transactional
    @CacheEvict(value = "infraestruturas", allEntries = true)
    public InfraestruturaResponse recolocarInfraestrutura(UUID infraId,
                                                        RecolocarInfraRequest request) {
        verificarAdmin(request.getEmailGestor());

        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        Local localPrincipal = infra.getLocais().stream()
            .filter(l -> l.getNome().equals("Local principal"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Local principal não encontrado"));

        CoordenadaGps gps = localPrincipal.getCoordenadaGps();
        if (gps == null) {
            throw new RuntimeException("Infraestrutura não tem coordenadas GPS");
        }

        
        gps.setLatitude(request.getNovaLatitude());
        gps.setLongitude(request.getNovaLongitude());
        gps.setRaio(request.getNovoRaio());
        gpsRepository.save(gps);

        log.info("Infraestrutura '{}' recolocada: lat={}, lon={}, raio={}",
                infra.getNome(), request.getNovaLatitude(),
                request.getNovaLongitude(), request.getNovoRaio());

        return obterInfoInfraestrutura(infraId);
    }

    @SuppressWarnings("null")
    @Transactional
    @CacheEvict(value = "infraestruturas", allEntries = true)
    public InfraestruturaResponse redimensionarInfraestrutura(UUID infraId,
                                                            RedimensionarInfraRequest request) {
        verificarAdmin(request.getEmailGestor());

        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        if (request.getNovaCapacidade() < 1) {
            throw new RuntimeException("Capacidade deve ser pelo menos 1");
        }

        infra.setCapacidade(request.getNovaCapacidade());
        infra.setBonusEntrega(request.getNovoBonusEntrega());
        infra.setCustoPost(request.getNovoCustoPost());
        infraRepository.save(infra);

        log.info("Infraestrutura '{}' redimensionada: cap={}, bonus={}, custo={}",
                infra.getNome(), request.getNovaCapacidade(),
                request.getNovoBonusEntrega(), request.getNovoCustoPost());

        return obterInfoInfraestrutura(infraId);
    }
   
    private void verificarAdmin(String emailGestor) {
    Utilizador gestor = utilizadorRepository.findByEmail(emailGestor)
        .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));
    if (!"ADMIN".equals(gestor.getRole())) {
        throw new RuntimeException("Apenas gestores podem realizar esta operação");
    }
    }

    public List<InfraestruturaResponse> listarTodasInfraestruturas() {
        return infraRepository.findAll().stream()
                .map(infra -> InfraestruturaResponse.builder()
                        .id(infra.getIdInfraestrutura())
                        .nome(infra.getNome())
                        .capacidade(infra.getCapacidade())
                        .bonusEntrega(infra.getBonusEntrega())
                        .custoPost(infra.getCustoPost())
                        .totalAnuncios(infra.getTotalAnuncios())
                        .totalEntregas(infra.getTotalEntregas())
                        .conexoesAtuais(infra.getTotalConexoes())
                        .build())
                .toList();
    }
}