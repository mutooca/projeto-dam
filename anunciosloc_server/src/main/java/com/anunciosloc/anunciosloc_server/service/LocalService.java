package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.CriarLocalRequest;
import com.anunciosloc.anunciosloc_server.dto.LocalResponse;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalService {

    private final LocalRepository localRepository;
    private final AnuncioRepository anuncioRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final InfraestruturaRepository infraRepository;
    private final CoordenadaGpsRepository  gpsRepository;
    private final CoordenadaWifiRepository wifiRepository;
    private final InfraDisponibilidadeService infraDisponibilidade;

    @Transactional
    @CacheEvict(value = "locais", allEntries = true)
    public LocalResponse criarLocalAutomatico(CriarLocalRequest request,
                                            double latUtilizador,
                                            double lonUtilizador) {
        Infraestrutura infraAssociada = null;

        List<Infraestrutura> todasInfras = infraRepository.findByAtivaTrue();

        for (Infraestrutura infra : todasInfras) {
            for (Local localExistente : infra.getLocais()) {
                if (localExistente.getCoordenadaGps() == null) continue;

                CoordenadaGps gpsInfra = localExistente.getCoordenadaGps();
                double distancia = HaversineUtil.calcularDistancia(
                        latUtilizador, lonUtilizador,
                        gpsInfra.getLatitude(), gpsInfra.getLongitude()
                );

                if (distancia <= gpsInfra.getRaio()) {
                    infraAssociada = infra;
                    break;
                }
            }
            if (infraAssociada != null) break;
        }

        if (infraAssociada == null) {
            throw new RuntimeException(
                "Não está dentro do raio de cobertura de nenhuma infraestrutura. " +
                "Apenas pode criar locais dentro de áreas de cobertura.");
        }

        Utilizador criador = utilizadorRepository.findByEmail(request.getEmailUtilizador())
             .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        infraDisponibilidade.verificarDisponibilidade(infraAssociada.getNome());

        boolean nomeJaExiste = infraAssociada.getLocais().stream()
        .anyMatch(l -> l.getNome().equalsIgnoreCase(request.getNome()));

        if (nomeJaExiste) {
            throw new RuntimeException(
                "Já existe um local com o nome '" + request.getNome() +
                "' nesta infraestrutura.");
        }

        
        boolean localDentroDoRaio = false;
        for (Local localExistente : infraAssociada.getLocais()) {
            if (localExistente.getCoordenadaGps() == null) continue;
            CoordenadaGps gpsInfra = localExistente.getCoordenadaGps();
            double distancia = HaversineUtil.calcularDistancia(
                    request.getLatitude(), request.getLongitude(),
                    gpsInfra.getLatitude(), gpsInfra.getLongitude()
            );
            if (distancia <= gpsInfra.getRaio()) {
                localDentroDoRaio = true;
                break;
            }
        }

        if (!localDentroDoRaio) {
            throw new RuntimeException(
                "As coordenadas do local não estão dentro do raio da infraestrutura.");
        }

        
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
        local.setInfraestrutura(infraAssociada);
        local.setCoordenadaGps(gps);
        local.setCoordenadaWifi(wifi);
        local.setCriadoPor(criador);
        local = localRepository.save(local);

        log.info("Local '{}' criado e associado à infraestrutura '{}'",
                local.getNome(), infraAssociada.getNome());

        return LocalResponse.builder()
                .idLocal(local.getIdLocal().toString())
                .nome(local.getNome())
                .latitude(gps != null ? gps.getLatitude() : 0)
                .longitude(gps != null ? gps.getLongitude() : 0)
                .raio(gps != null ? gps.getRaio() : 0)
                .build();
    }

   
   /*private Infraestrutura encontrarInfraestrutura(Double lat, Double lon) {
        if (lat == null || lon == null) return null;

        List<Infraestrutura> todasInfras = infraRepository.findByAtivaTrue();

        for (Infraestrutura infra : todasInfras) {
            for (Local local : infra.getLocais()) {
                if (local.getCoordenadaGps() == null) continue;

                CoordenadaGps gps = local.getCoordenadaGps();
                double distancia = HaversineUtil.calcularDistancia(
                        lat, lon,
                        gps.getLatitude(), gps.getLongitude()
                );

                
                if (distancia <= gps.getRaio()) {
                    return infra;
                }
            }
        }
        return null;
    }*/

    @Cacheable(value = "locais", key = "#lat + ',' + #lon")
    public List<LocalResponse> listarLocaisProximos(double latUtilizador,
                                                    double lonUtilizador) {

        List<Infraestrutura> todasInfras = infraRepository.findByAtivaTrue();

        return todasInfras.stream()
            .filter(infra -> infra.getLocais().stream()
                .filter(l -> l.getCoordenadaGps() != null)
                .anyMatch(l -> HaversineUtil.calcularDistancia(
                        latUtilizador, lonUtilizador,
                        l.getCoordenadaGps().getLatitude(),
                        l.getCoordenadaGps().getLongitude())
                    <= l.getCoordenadaGps().getRaio()))
            // infra cobre o utilizador — devolve os seus locais
            .flatMap(infra -> infra.getLocais().stream()
                .filter(l -> l.getCoordenadaGps() != null)
                .filter(l -> HaversineUtil.calcularDistancia(
                        latUtilizador, lonUtilizador,
                        l.getCoordenadaGps().getLatitude(),
                        l.getCoordenadaGps().getLongitude())
                    <= l.getCoordenadaGps().getRaio())
                .map(l -> LocalResponse.builder()
                        .idLocal(l.getIdLocal().toString())
                        .nome(l.getNome())
                        .latitude(l.getCoordenadaGps().getLatitude())
                        .longitude(l.getCoordenadaGps().getLongitude())
                        .raio(l.getCoordenadaGps().getRaio())
                        .build()))
            .toList();
    }

    @SuppressWarnings("null")
    @Transactional
    @CacheEvict(value = "locais", allEntries = true)
    public void removerLocal(UUID localId, String emailUtilizador) {

        Utilizador utilizador = utilizadorRepository.findByEmail(emailUtilizador)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        Local local = localRepository.findById(localId)
            .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        if (!local.getCriadoPor().getIdUtilizador()
                .equals(utilizador.getIdUtilizador())) {
            throw new RuntimeException(
                "Apenas o criador do local pode eliminá-lo.");
        }

        boolean temAnunciosActivos = anuncioRepository
            .findByLocal(local).stream()
            .anyMatch(a -> "ATIVO".equals(a.getEstado()));

        if (temAnunciosActivos) {
            throw new RuntimeException(
                "Não é possível eliminar um local com anúncios activos.");
        }

        localRepository.delete(local);
        log.info("Local '{}' eliminado por '{}'", local.getNome(), emailUtilizador);
    }

}