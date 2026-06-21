package com.uan.anunciosloc.infrastructura_server.soap;


import com.uan.anunciosloc.infrastructura_server.model.*;
import com.uan.anunciosloc.infrastructura_server.service.InfraEstadoService;
import com.uan.anunciosloc.infrastructura_server.soap.dto.*;

import jakarta.jws.WebService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@WebService(
    
    serviceName       = "InfrastructureService",
    portName          = "InfrastructurePort",
    targetNamespace   = "http://infrastructura.anunciosloc.uan.com"
)
public class InfraestruturaServiceImpl implements InfraestruturaServiceSEI {

    private final InfraEstadoService estado;

    
    @Override
    public InfraInfoResponse obterInfoInfraestrutura() {
        log.debug("obterInfoInfraestrutura chamado");

        return InfraInfoResponse.builder()
                .nome(estado.getInfraNome())
                .urlEndpoint(estado.getPublicUrl())
                .capacidade(estado.getCapacidade())
                .bonusEntrega(estado.getBonusEntrega())
                .custoPost(estado.getCustoPost())
                .totalAnuncios(estado.getTotalAnuncios())
                .totalEntregas(estado.getTotalEntregas())
                .totalConexoes(estado.getTotalConexoes())
                .conectadosAgora(estado.getConectadosAgora())
                .conexoesDisponiveis(
                    estado.getCapacidade() - estado.getConectadosAgora())
                .build();
    }

    
    @Override
    public CriarLocalResponse criarLocal(CriarLocalRequest request) {
        log.debug("criarLocal: nome={}, tipo={}", 
                  request.getNome(), request.getTipoCoordenada());

        // Valida tipo
        if (request.getTipoCoordenada() == null ||
            (!request.getTipoCoordenada().equalsIgnoreCase("GPS") &&
             !request.getTipoCoordenada().equalsIgnoreCase("WIFI"))) {
            return CriarLocalResponse.builder()
                    .sucesso(false)
                    .mensagem("tipoCoordenada deve ser GPS ou WIFI")
                    .build();
        }

        LocalInfo local = LocalInfo.builder()
                .nome(request.getNome())
                .tipoCoordenada(request.getTipoCoordenada().toUpperCase())
                .build();

        // GPS
        if ("GPS".equalsIgnoreCase(request.getTipoCoordenada())) {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                return CriarLocalResponse.builder()
                        .sucesso(false)
                        .mensagem("latitude e longitude obrigatórios para tipo GPS")
                        .build();
            }
            local.setCoordenadaGps(CoordenadaGpsInfo.builder()
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .raioMetros(request.getRaioMetros() != null 
                                ? request.getRaioMetros() : 20.0)
                    .build());
        }

        // WIFI
        if ("WIFI".equalsIgnoreCase(request.getTipoCoordenada())) {
            if (request.getSsids() == null || request.getSsids().isBlank()) {
                return CriarLocalResponse.builder()
                        .sucesso(false)
                        .mensagem("ssids obrigatório para tipo WIFI")
                        .build();
            }
            List<CoordenadaWifiInfo> wifis = Arrays.stream(
                    request.getSsids().split(","))
                    .map(ssid -> CoordenadaWifiInfo.builder()
                                     .ssid(ssid.trim())
                                     .build())
                    .collect(Collectors.toList());
            local.setCoordenadasWifi(wifis);
        }

        LocalInfo criado = estado.criarLocal(local);

        return CriarLocalResponse.builder()
                .idLocal(criado.getId())
                .sucesso(true)
                .mensagem("Local criado com sucesso")
                .build();
    }

    
    @Override
    public DefinirRestricaoResponse definirRestricao(DefinirRestricaoRequest request) {
        log.debug("definirRestricao: tipo={}", request.getTipoRestricao());

        if (request.getTipoRestricao() == null || request.getTipoRestricao().isBlank()) {
            return DefinirRestricaoResponse.builder()
                    .sucesso(false)
                    .mensagem("tipoRestricao é obrigatório")
                    .build();
        }

        RestricaoInfo restricao = RestricaoInfo.builder()
                .tipoRestricao(request.getTipoRestricao())
                .valorRestricao(request.getValorRestricao())
                .descricao(request.getDescricao())
                .build();

        RestricaoInfo criada = estado.adicionarRestricao(restricao);

        return DefinirRestricaoResponse.builder()
                .idRestricao(criada.getId())
                .sucesso(true)
                .mensagem("Restrição definida com sucesso")
                .build();
    }

   
    @Override
    public ObterSaldoResponse obterSaldo(String idUtilizador) {
        log.debug("obterSaldo: utilizador={}", idUtilizador);

        SaldoReplicaInfo saldo = estado.lerSaldo(idUtilizador);

        return ObterSaldoResponse.builder()
                .idUtilizador(idUtilizador)
                .saldo(saldo.getSaldo())
                .encontrado(saldo.getVersao() > 0)
                .build();
    }

   
    @Override
    public LerSaldoResponse lerSaldo(String idUtilizador) {
        log.debug("lerSaldo: utilizador={}", idUtilizador);

        SaldoReplicaInfo saldo = estado.lerSaldo(idUtilizador);

        return LerSaldoResponse.builder()
                .idUtilizador(idUtilizador)
                .saldo(saldo.getSaldo())
                .versao(saldo.getVersao())
                .encontrado(saldo.getVersao() > 0)
                .build();
    }

    
    @Override
    public EscreverSaldoResponse escreverSaldo(String idUtilizador,
                                                float novoSaldo,
                                                int versao) {
        log.debug("escreverSaldo: utilizador={}, saldo={}, versao={}",
                  idUtilizador, novoSaldo, versao);

        boolean sucesso = estado.escreverSaldo(idUtilizador, novoSaldo, versao);

        if (!sucesso) {
            SaldoReplicaInfo actual = estado.lerSaldo(idUtilizador);
            return EscreverSaldoResponse.builder()
                    .sucesso(false)
                    .versaoActual(actual.getVersao())
                    .mensagem("Versão desactualizada. Versão actual: "
                              + actual.getVersao())
                    .build();
        }

        return EscreverSaldoResponse.builder()
                .sucesso(true)
                .versaoActual(versao)
                .mensagem("Saldo actualizado com sucesso")
                .build();
    }

    
    @Override
    public String ping() {
        return String.format(
            "PONG | %s | URL: %s | Capacidade: %d | Conectados: %d | " +
            "Anúncios: %d | Entregas: %d | Estado: OK",
            estado.getInfraNome(),
            estado.getPublicUrl(),
            estado.getCapacidade(),
            estado.getConectadosAgora(),
            estado.getTotalAnuncios(),
            estado.getTotalEntregas()
        );
    }

    
    @Override
    public void clear() {
        log.warn("clear() chamado em {}", estado.getInfraNome());
        estado.clear();
    }

    
    @Override
    public void initInfraestrutura(int capacidade, int bonusEntrega, int custoPost) {
        log.info("initInfraestrutura: cap={}, bonus={}, custo={}",
                 capacidade, bonusEntrega, custoPost);
       
    }
}
