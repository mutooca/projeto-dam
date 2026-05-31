package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfraestruturaService {

    private final InfraestruturaRepository infraRepository;
    private final LocalRepository localRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final CoordenadaGpsRepository gpsRepository;
    private final CoordenadaWifiRepository wifiRepository;
    private final RegistoEstatisticoRepository estatisticoRepository;

    
    @Transactional
    public Infraestrutura registarInfraestrutura(RegistarInfraRequest request) {
        Utilizador gestor = utilizadorRepository.findByEmail(request.getEmailGestor())
            .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        if (!"ADMIN".equals(gestor.getRole())) {
            throw new RuntimeException("Apenas gestores podem criar infraestruturas");
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

    
    public List<InfraestruturaResponse> listarInfraestruturasProximas(double lat, double lon, int k) {
        List<Infraestrutura> todas = infraRepository.findByAtivaTrue();

        return todas.stream()
            .map(infra -> {
                // Calcula a distância mínima entre o user(nesste caso app mobile) e qualquer local da infraestrutura
                double distancia = infra.getLocais().stream()
                    .filter(l -> l.getCoordenadaGps() != null)
                    .mapToDouble(l -> HaversineUtil.calcularDistancia(
                        lat, lon,
                        l.getCoordenadaGps().getLatitude(),
                        l.getCoordenadaGps().getLongitude()))
                    .min()
                    .orElse(Double.MAX_VALUE);

                
                return InfraestruturaResponse.builder()
                    .id(infra.getIdInfraestrutura())
                    .nome(infra.getNome() != null ? infra.getNome() : "Sem nome")
                    .distanciaKm(distancia)
                    .capacidade(infra.getCapacidade())
                    .bonusEntrega(infra.getBonusEntrega())
                    .custoPost(infra.getCustoPost())
                    .conexoesAtuais(infra.getTotalConexoes())
                    .build();
            })
            .filter(r -> r.getDistanciaKm() < Double.MAX_VALUE)
            .sorted(Comparator.comparingDouble(InfraestruturaResponse::getDistanciaKm))
            .limit(k)
            .collect(Collectors.toList());
    }

   
    @Transactional
    public Local criarLocal(@NonNull UUID infraId, CriarLocalRequest request) {
        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

        CoordenadaGps gps = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            gps = new CoordenadaGps();
            gps.setLatitude(request.getLatitude());
            gps.setLongitude(request.getLongitude());
            gps.setRaio(request.getRaio());
            gps = gpsRepository.save(gps);
        }

        CoordenadaWifi wifi = null;
        if (request.getSsidWifi() != null && !request.getSsidWifi().isEmpty()) {
            wifi = new CoordenadaWifi();
            wifi.setSsid(request.getSsidWifi());
            wifi = wifiRepository.save(wifi);
        }

        Local local = new Local();
        local.setNome(request.getNome());
        local.setInfraestrutura(infra);
        local.setCoordenadaGps(gps);
        local.setCoordenadaWifi(wifi);

        return localRepository.save(local);
    }
}