package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilizadorRepository utilizadorRepository;
    private final InfraestruturaRepository infraRepository;
    private final AnuncioRepository anuncioRepository;
    private final EntregaAnuncioRepository entregaRepository;
    private final ConexaoRepository conexaoRepository;
    private final UddiClient uddiClient;

    public DashboardResponse obterDashboard(String emailGestor) {

        
        var gestor = utilizadorRepository.findByEmail(emailGestor)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        if (!"ADMIN".equals(gestor.getRole())) {
            throw new RuntimeException("Apenas gestores podem aceder ao dashboard");
        }

        
        var todosUtilizadores = utilizadorRepository.findAll().stream()
                .filter(u -> !"ADMIN".equals(u.getRole()))
                .toList();

        int totalUtilizadores = todosUtilizadores.size();
        int utilizadoresAtivos = (int) todosUtilizadores.stream()
                .filter(u -> u.isAtivo()).count();
        int utilizadoresInativos = totalUtilizadores - utilizadoresAtivos;

       
        var todasInfras = infraRepository.findAll();
        int totalInfras = todasInfras.size();
        int infrasActivas = (int) todasInfras.stream()
                .filter(i -> i.isAtiva()).count();

        
        int infrasNoUddi = 0;
        try {
            infrasNoUddi = uddiClient.descobrirInfraestruturas().size();
        } catch (Exception e) {
            log.warn("Não foi possível contactar UDDI: {}", e.getMessage());
        }

        
        int conexoesActivas = (int) conexaoRepository.count();

        
        var todosAnuncios = anuncioRepository.findAll();
        int totalAnuncios = todosAnuncios.size();
        int anunciosAtivos = (int) todosAnuncios.stream()
                .filter(a -> "ATIVO".equals(a.getEstado())).count();
        int anunciosRemovidos = (int) todosAnuncios.stream()
                .filter(a -> "REMOVIDO".equals(a.getEstado())).count();

        
        var todasEntregas = entregaRepository.findAll();
        int totalEntregues = todasEntregas.size();
        int totalLidos = (int) todasEntregas.stream()
                .filter(e -> "LIDO".equals(e.getEstadoEntrega())).count();

        
        List<InfraestruturaResponse> listaInfras = todasInfras.stream()
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

        return DashboardResponse.builder()
                .nomeGestor(gestor.getNome())
                .emailGestor(gestor.getEmail())
                .totalUtilizadores(totalUtilizadores)
                .utilizadoresAtivos(utilizadoresAtivos)
                .utilizadoresInativos(utilizadoresInativos)
                .totalInfraestruturas(totalInfras)
                .infraestruturasActivas(infrasActivas)
                .infraestruturasNoUddi(infrasNoUddi)
                .totalConexoesActivas(conexoesActivas)
                .totalAnuncios(totalAnuncios)
                .totalAnunciosAtivos(anunciosAtivos)
                .totalAnunciosEntregues(totalEntregues)
                .totalAnunciosLidos(totalLidos)
                .totalAnunciosRemovidos(anunciosRemovidos)
                .infraestruturas(listaInfras)
                .build();
    }
}