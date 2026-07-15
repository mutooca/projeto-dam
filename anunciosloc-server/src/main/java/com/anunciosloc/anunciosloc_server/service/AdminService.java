package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.admin.*;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.UddiClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ObterSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminService {

    private final InfraestruturaRepository infraRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final UddiClient uddiClient;
    private final InfrastruturaSoapClient soapClient;

    public DashboardEstatisticasDTO obterDashboardEstatisticas() {
        log.info(" [ADMIN] Obtendo estatísticas do dashboard");

        long totalUtilizadores = utilizadorRepository.countByRole("USER");
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
        long utilizadoresAtivos = utilizadorRepository.countActiveUsers(dataLimite);
        long utilizadoresInativos = totalUtilizadores - utilizadoresAtivos;

        List<Infraestrutura> infrasBD = infraRepository.findAll();
        long totalInfraestruturas = infrasBD.size();
        log.info(" Infraestruturas na BD: {}", totalInfraestruturas);

        for (Infraestrutura infra : infrasBD) {
            log.info("   - {} : {} (ativa: {})", infra.getNome(), infra.getUrl(), infra.isAtiva());
        }

        List<InfraestruturaAdminDTO> infrasStatus = verificarStatusInfraestruturas(infrasBD);
        long infraestruturasOnline = infrasStatus.stream()
                .filter(InfraestruturaAdminDTO::getOnline)
                .count();
        long infraestruturasOffline = totalInfraestruturas - infraestruturasOnline;
        log.info(" Infras online: {}, offline: {}", infraestruturasOnline, infraestruturasOffline);

        long totalLocais = 0;
        long totalAnuncios = 0;
        long totalEntregas = 0;
        long totalConexoes = 0;

        List<InfraProxy> infras = soapClient.obterClientes();
        log.info(" Clientes SOAP obtidos: {}", infras.size());

        for (InfraProxy proxy : infras) {
            try {
                log.info("   Chamando obterInfoInfraestrutura para: {}", proxy.getServiceUrl());
                InfraInfoResponse info = proxy.obterInfoInfraestrutura();

                if (info != null && info.getNome() != null && !info.getNome().isEmpty()) {
                    log.info("  Info obtida: nome={}, locais={}, anuncios={}, entregas={}, conexoes={}",
                            info.getNome(),
                            info.getTotalLocais(),
                            info.getTotalAnuncios(),
                            info.getTotalEntregas(),
                            info.getTotalConexoes());

                    totalLocais += info.getTotalLocais() != null ? info.getTotalLocais() : 0;
                    totalAnuncios += info.getTotalAnuncios() != null ? info.getTotalAnuncios() : 0;
                    totalEntregas += info.getTotalEntregas() != null ? info.getTotalEntregas() : 0;
                    totalConexoes += info.getTotalConexoes() != null ? info.getTotalConexoes() : 0;
                } else {

                    String erro = info != null ? info.getMensagem() : "resposta nula";
                    log.warn(" Info inválida para {}: {}", proxy.getServiceUrl(), erro);
                }
            } catch (Exception e) {
                log.error("  Erro ao obter info da infra: {}", e.getMessage());
            }
        }

        log.info(" [ADMIN] Totais finais: locais={}, anuncios={}, entregas={}, conexoes={}",
                totalLocais, totalAnuncios, totalEntregas, totalConexoes);

        return DashboardEstatisticasDTO.builder()
                .totalUtilizadores(totalUtilizadores)
                .utilizadoresAtivos(utilizadoresAtivos)
                .utilizadoresInativos(utilizadoresInativos)
                .totalInfraestruturas(totalInfraestruturas)
                .infraestruturasOnline(infraestruturasOnline)
                .infraestruturasOffline(infraestruturasOffline)
                .totalLocais(totalLocais)
                .totalAnuncios(totalAnuncios)
                .totalEntregas(totalEntregas)
                .totalConexoes(totalConexoes)
                .ultimaAtualizacao(LocalDateTime.now())
                .build();
    }

    public List<InfraestruturaAdminDTO> listarTodasInfraestruturas() {
        log.info(" [ADMIN] Listando todas as infraestruturas");

        List<Infraestrutura> infrasBD = infraRepository.findAll();
        List<UddiRecord> infrasUDDI = uddiClient.descobrirInfraestruturas();
        Set<String> urlsUDDI = infrasUDDI.stream()
                .map(UddiRecord::getServiceUrl)
                .collect(Collectors.toSet());

        Map<String, Integer> conexoesPorInfra = new HashMap<>();
        List<InfraProxy> infras = soapClient.obterClientes();
        for (InfraProxy proxy : infras) {
            try {
                var info = proxy.obterInfoInfraestrutura();
                if (info != null && info.getNome() != null) {
                    conexoesPorInfra.put(info.getNome(),
                            info.getTotalConexoes() != null ? info.getTotalConexoes() : 0);
                }
            } catch (Exception e) {
                log.warn(" Erro ao obter conexões da infra: {}", e.getMessage());
            }
        }

        List<InfraestruturaAdminDTO> result = new ArrayList<>();

        for (Infraestrutura infra : infrasBD) {
            boolean estaNoUDDI = urlsUDDI.contains(infra.getUrl() + "/ws/InfrastructureService");
            boolean estaOnline = estaNoUDDI && verificarPing(infra.getUrl());

            Integer conexoes = conexoesPorInfra.getOrDefault(infra.getNome(), 0);

            result.add(InfraestruturaAdminDTO.builder()
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
                    .online(estaOnline)
                    .registadaUDDI(estaNoUDDI)
                    .conexoesAtuais(conexoes)
                    .build());
        }

        return result;
    }

    public List<InfraestruturaAdminDTO> listarInfraestruturasNaoRegistadas() {
        log.info(" [ADMIN] Listando infraestruturas no UDDI mas NÃO registadas na BD");

        List<UddiRecord> infrasUDDI = uddiClient.descobrirInfraestruturas();
        List<String> nomesBD = infraRepository.findAll().stream()
                .map(Infraestrutura::getNome)
                .collect(Collectors.toList());

        List<InfraestruturaAdminDTO> result = new ArrayList<>();

        for (UddiRecord record : infrasUDDI) {
            if (!nomesBD.contains(record.getServiceName())) {
                result.add(InfraestruturaAdminDTO.builder()
                        .nome(record.getServiceName())
                        .url(record.getServiceUrl())
                        .online(true)
                        .registadaUDDI(true)
                        .ativa(false)
                        .build());
            }
        }

        return result;
    }

    public List<UtilizadorAdminDTO> listarTodosUtilizadores() {
        log.info(" [ADMIN] Listando todos os utilizadores (apenas USER)");

        List<Utilizador> utilizadores = utilizadorRepository.findByRole("USER");

        if (utilizadores.isEmpty()) {
            log.info(" Nenhum utilizador USER encontrado");
            return List.of();
        }

        List<UtilizadorAdminDTO> result = new ArrayList<>();
        List<InfraProxy> infras = soapClient.obterClientes();

        for (Utilizador user : utilizadores) {
            String email = user.getEmail();
            Integer saldo = user.getSaldo();
            String ultimaLocalizacao = user.getUltimaLocalizacao() != null
                    ? user.getUltimaLocalizacao()
                    : "desconhecida";

            LocalDateTime ultimoPost = null;
            Integer totalAnuncios = 0;
            Integer totalEntregas = 0;

            for (InfraProxy proxy : infras) {
                try {

                    ObterSaldoResponse saldoResponse = proxy.obterSaldo(email);
                    if (saldoResponse != null && saldoResponse.isSucesso()) {
                        saldo = (int) saldoResponse.getSaldo();
                    }

                    try {
                        String ultimoPostStr = proxy.obterUltimoPost(email);
                        if (ultimoPostStr != null && !ultimoPostStr.isEmpty()) {
                            ultimoPost = LocalDateTime.parse(ultimoPostStr);
                        }
                    } catch (Exception e) {
                        log.debug(" obterUltimoPost não disponível: {}", e.getMessage());
                    }

                    try {
                        totalAnuncios = (int) proxy.contarAnunciosPorEmail(email);
                    } catch (Exception e) {
                        log.debug(" contarAnunciosPorEmail não disponível: {}", e.getMessage());
                    }

                    try {
                        totalEntregas = (int) proxy.contarEntregasPorEmail(email);
                    } catch (Exception e) {
                        log.debug(" contarEntregasPorEmail não disponível: {}", e.getMessage());
                    }

                } catch (Exception e) {
                    log.warn(" Erro ao buscar dados de {}: {}", email, e.getMessage());
                }
            }

            Long diasInativo = calcularDiasInativo(ultimoPost);
            String status = (ultimoPost != null &&
                    ultimoPost.isAfter(LocalDateTime.now().minusDays(7)))
                            ? "ATIVO"
                            : "INATIVO";

            result.add(UtilizadorAdminDTO.builder()
                    .email(user.getEmail())
                    .nome(user.getNome())
                    .saldo(saldo)
                    .ultimoPost(ultimoPost)
                    .diasInativo(diasInativo)
                    .status(status)
                    .ultimaLocalizacao(ultimaLocalizacao)
                    .totalAnuncios(totalAnuncios)
                    .totalEntregas(totalEntregas)
                    .build());
        }

        log.info(" {} utilizadores USER encontrados", result.size());
        return result;
    }

    public List<UtilizadorAdminDTO> listarUtilizadoresInativos() {
        return listarTodosUtilizadores().stream()
                .filter(u -> "INATIVO".equals(u.getStatus()))
                .collect(Collectors.toList());
    }

    public List<UtilizadorAdminDTO> listarUtilizadoresAtivos() {
        return listarTodosUtilizadores().stream()
                .filter(u -> "ATIVO".equals(u.getStatus()))
                .collect(Collectors.toList());
    }

    public long contarUtilizadores() {
        return utilizadorRepository.count();
    }

    public long contarUtilizadoresAtivos() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
        return utilizadorRepository.countActiveUsers(dataLimite);
    }

    public long contarUtilizadoresInativos() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
        return utilizadorRepository.countInactiveUsers(dataLimite);
    }

    private List<InfraestruturaAdminDTO> verificarStatusInfraestruturas(List<Infraestrutura> infras) {
        log.info(" [ADMIN] Verificando status das infraestruturas...");

        List<InfraestruturaAdminDTO> result = new ArrayList<>();

        List<UddiRecord> infrasUDDI = uddiClient.descobrirInfraestruturas();
        log.info("   Infras no UDDI: {}", infrasUDDI.size());

        Set<String> urlsUDDI = infrasUDDI.stream()
                .map(UddiRecord::getServiceUrl)
                .collect(Collectors.toSet());

        for (Infraestrutura infra : infras) {
            String urlCompleta = infra.getUrl() + "/ws/InfrastructureService";
            boolean estaNoUDDI = urlsUDDI.contains(urlCompleta);
            boolean estaOnline = estaNoUDDI && verificarPing(infra.getUrl());

            log.info("   Infra '{}' - UDDI: {}, Ping: {}",
                    infra.getNome(), estaNoUDDI, estaOnline);

            result.add(InfraestruturaAdminDTO.builder()
                    .id(infra.getId())
                    .nome(infra.getNome())
                    .online(estaOnline)
                    .registadaUDDI(estaNoUDDI)
                    .build());
        }

        return result;
    }

    private boolean verificarPing(String baseUrl) {
        try {
            String url = baseUrl + "/ws/InfrastructureService";
            InfraProxy proxy = soapClient.criarProxy(url);
            String ping = proxy.ping();
            return ping != null && ping.startsWith("PONG");
        } catch (Exception e) {
            return false;
        }
    }

    private Long calcularDiasInativo(LocalDateTime ultimoPost) {
        if (ultimoPost == null)
            return 999L;
        return java.time.temporal.ChronoUnit.DAYS.between(ultimoPost, LocalDateTime.now());
    }

    public AdminInfoDTO obterAdminInfo(String email) {
        log.info(" [ADMIN] Obtendo informações do admin: {}", email);

        Utilizador admin = utilizadorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        return AdminInfoDTO.builder()
                .nome(admin.getNome())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }

}