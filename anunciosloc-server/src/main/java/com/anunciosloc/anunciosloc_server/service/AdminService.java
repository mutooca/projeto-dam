package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

        private final UtilizadorRepository utilizadorRepository;
        private final InfraestruturaRepository infraRepository;
        private final UddiClient uddiClient;
        private final InfrastruturaSoapClient soapClient;

        @SuppressWarnings("null")
        public DashboardResponse obterDashboard(String emailGestor) {
                log.info(" Gestor {} a aceder ao dashboard", emailGestor);

                Utilizador gestor = utilizadorRepository.findByEmail(emailGestor)
                                .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

                if (!"ADMIN".equals(gestor.getRole())) {
                        throw new RuntimeException("Apenas gestores podem aceder ao dashboard");
                }

                List<Utilizador> todosUtilizadores = utilizadorRepository.findAll().stream()
                                .filter(u -> !"ADMIN".equals(u.getRole()))
                                .toList();

                int totalUtilizadores = todosUtilizadores.size();
                int utilizadoresAtivos = (int) todosUtilizadores.stream()
                                .filter(Utilizador::isAtivo).count();
                int utilizadoresInativos = totalUtilizadores - utilizadoresAtivos;

                List<Infraestrutura> todasInfras = infraRepository.findAll();
                int totalInfras = todasInfras.size();
                int infrasActivas = (int) todasInfras.stream()
                                .filter(Infraestrutura::isAtiva).count();

                int infrasNoUddi = 0;
                try {
                        infrasNoUddi = uddiClient.descobrirInfraestruturas().size();
                } catch (Exception e) {
                        log.warn(" Não foi possível contactar UDDI: {}", e.getMessage());
                }

                int totalConexoesAtivas = 0;
                int totalAnuncios = 0;
                int totalAnunciosAtivos = 0;
                int totalEntregues = 0;
                int totalLidos = 0;
                int totalAnunciosRemovidos = 0;

                List<InfraestruturaResponse> listaInfras = new ArrayList<>();

                for (Infraestrutura infra : todasInfras) {
                        try {

                                InfraProxy proxy = soapClient.obterClientePorNome(infra.getNome());
                                if (proxy != null) {
                                        InfraInfoResponse info = proxy.obterInfoInfraestrutura();

                                        // Acumular estatísticas
                                        totalConexoesAtivas += info.getTotalConexoes() != null ? info.getTotalConexoes()
                                                        : 0;
                                        totalAnuncios += info.getTotalAnuncios() != null ? info.getTotalAnuncios() : 0;
                                        totalAnunciosAtivos += info.getTotalAnuncios() != null ? info.getTotalAnuncios()
                                                        : 0;
                                        totalEntregues += info.getTotalEntregas() != null ? info.getTotalEntregas() : 0;

                                        listaInfras.add(InfraestruturaResponse.builder()
                                                        .id(infra.getId())
                                                        .nome(infra.getNome())
                                                        .latitude(infra.getLatitude())
                                                        .longitude(infra.getLongitude())
                                                        .raio(infra.getRaio())
                                                        .capacidade(infra.getCapacidade())
                                                        .bonusEntrega(infra.getBonusEntrega())
                                                        .custoPost(infra.getCustoPost())
                                                        .totalAnuncios(info.getTotalAnuncios())
                                                        .totalEntregas(info.getTotalEntregas())
                                                        .conexoesAtuais(info.getTotalConexoes())
                                                        .build());
                                } else {

                                        listaInfras.add(InfraestruturaResponse.builder()
                                                        .id(infra.getId())
                                                        .nome(infra.getNome())
                                                        .latitude(infra.getLatitude())
                                                        .longitude(infra.getLongitude())
                                                        .raio(infra.getRaio())
                                                        .capacidade(infra.getCapacidade())
                                                        .bonusEntrega(infra.getBonusEntrega())
                                                        .custoPost(infra.getCustoPost())
                                                        .totalAnuncios(0)
                                                        .totalEntregas(0)
                                                        .conexoesAtuais(0)
                                                        .build());
                                }

                        } catch (Exception e) {
                                log.warn(" Erro ao obter info da infra {} via SOAP: {}", infra.getNome(),
                                                e.getMessage());
                                // Fallback: dados da BD
                                listaInfras.add(InfraestruturaResponse.builder()
                                                .id(infra.getId())
                                                .nome(infra.getNome())
                                                .latitude(infra.getLatitude())
                                                .longitude(infra.getLongitude())
                                                .raio(infra.getRaio())
                                                .capacidade(infra.getCapacidade())
                                                .bonusEntrega(infra.getBonusEntrega())
                                                .custoPost(infra.getCustoPost())
                                                .totalAnuncios(0)
                                                .totalEntregas(0)
                                                .conexoesAtuais(0)
                                                .build());
                        }
                }

                return DashboardResponse.builder()
                                .nomeGestor(gestor.getNome())
                                .emailGestor(gestor.getEmail())
                                .totalUtilizadores(totalUtilizadores)
                                .utilizadoresAtivos(utilizadoresAtivos)
                                .utilizadoresInativos(utilizadoresInativos)
                                .totalInfraestruturas(totalInfras)
                                .infraestruturasActivas(infrasActivas)
                                .infraestruturasNoUddi(infrasNoUddi)
                                .totalConexoesActivas(totalConexoesAtivas)
                                .totalAnuncios(totalAnuncios)
                                .totalAnunciosAtivos(totalAnunciosAtivos)
                                .totalAnunciosEntregues(totalEntregues)
                                .totalAnunciosLidos(totalLidos)
                                .totalAnunciosRemovidos(totalAnunciosRemovidos)
                                .infraestruturas(listaInfras)
                                .build();
        }
}