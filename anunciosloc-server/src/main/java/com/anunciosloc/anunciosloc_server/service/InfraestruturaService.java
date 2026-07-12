package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraestruturaService {

    private final InfrastruturaSoapClient soapClient;

    
    public List<InfraInfoSOAP> listarTodasInfras() {
        log.info(" [ANUNCIOSLOC] Listando todas as infraestruturas");

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            return new ArrayList<>();
        }

        List<InfraInfoSOAP> resultado = new ArrayList<>();

        for (InfraProxy infra : infras) {
            try {
                InfraInfoResponse info = infra.obterInfoInfraestrutura();
                
                InfraInfoSOAP infraInfo = InfraInfoSOAP.builder()
                    .id(info.getId())
                    .nome(info.getNome())
                    .url(info.getUrl())
                    .capacidade(info.getCapacidade())
                    .bonusEntrega(info.getBonusEntrega())
                    .custoPost(info.getCustoPost())
                    .ativa(info.isAtiva())
                    .totalLocais(info.getTotalLocais())
                    .totalAnuncios(info.getTotalAnuncios())
                    .totalEntregas(info.getTotalEntregas())
                    .totalConexoes(info.getTotalConexoes())
                    .latitude(info.getLatitude())
                    .longitude(info.getLongitude())
                    .raio(info.getRaio())
                    .build();
                
                resultado.add(infraInfo);
                
            } catch (Exception e) {
                log.warn("Erro ao obter info da infra {}: {}", infra.getServiceUrl(), e.getMessage());
            }
        }

        log.info("{} infraestruturas encontradas", resultado.size());
        return resultado;
    }

    
    public InfraInfoSOAP obterInfoInfra(String nome) {
        log.info(" [ANUNCIOSLOC] Obtendo info da infra: {}", nome);

        InfraProxy infra = soapClient.obterClientePorNome(nome);
        if (infra == null) {
            throw new RuntimeException("Infraestrutura não encontrada: " + nome);
        }

        InfraInfoResponse info = infra.obterInfoInfraestrutura();

        return InfraInfoSOAP.builder()
            .id(info.getId())
            .nome(info.getNome())
            .url(info.getUrl())
            .capacidade(info.getCapacidade())
            .bonusEntrega(info.getBonusEntrega())
            .custoPost(info.getCustoPost())
            .ativa(info.isAtiva())
            .totalLocais(info.getTotalLocais())
            .totalAnuncios(info.getTotalAnuncios())
            .totalEntregas(info.getTotalEntregas())
            .totalConexoes(info.getTotalConexoes())
            .latitude(info.getLatitude())
            .longitude(info.getLongitude())
            .raio(info.getRaio())
            .build();
    }
}