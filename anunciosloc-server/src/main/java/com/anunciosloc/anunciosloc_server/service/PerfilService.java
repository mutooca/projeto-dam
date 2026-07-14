package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.PerfilItem;
import com.anunciosloc.anunciosloc_server.dto.PerfilRequest;
import com.anunciosloc.anunciosloc_server.dto.PerfilResponse;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfilService {

    private final InfrastruturaSoapClient soapClient;

    public String adicionarPerfil(PerfilRequest request) {
        log.info(" [PERFIL] Adicionando perfil para: {}", request.getEmail());

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        for (InfraProxy infra : infras) {
            try {
                MensagemResponse response = infra.adicionarPerfil(
                        request.getEmail(),
                        request.getPerfil()
                );
                if (response != null && response.isSucesso()) {
                    log.info(" Perfil adicionado na infra: {}", infra.getServiceUrl());
                    return response.getMensagem();
                }
            } catch (Exception e) {
                log.warn(" Falha ao adicionar perfil na infra {}: {}", 
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível adicionar o perfil");
    }

    public PerfilResponse consultarPerfil(String email) {
        log.info(" [PERFIL] Consultando perfil para: {}", email);

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        for (InfraProxy infra : infras) {
            try {
                List<PerfilItem> perfil = infra.consultarPerfil(email);
                if (perfil != null) {
                    log.info("  Perfil obtido da infra: {}", infra.getServiceUrl());
                    return PerfilResponse.builder()
                            .email(email)
                            .perfil(perfil)
                            .mensagem("Perfil obtido com sucesso")
                            .build();
                }
            } catch (Exception e) {
                log.warn(" Falha ao consultar perfil na infra {}: {}", 
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        return PerfilResponse.builder()
                .email(email)
                .perfil(List.of())
                .mensagem("Nenhum perfil encontrado")
                .build();
    }

    public String removerChavePerfil(String email, String chave) {
        log.info(" [PERFIL] Removendo chave '{}' para: {}", chave, email);

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        for (InfraProxy infra : infras) {
            try {
                MensagemResponse response = infra.removerChavePerfil(email, chave);
                if (response != null && response.isSucesso()) {
                    log.info("  Chave removida na infra: {}", infra.getServiceUrl());
                    return response.getMensagem();
                }
            } catch (Exception e) {
                log.warn(" Falha ao remover chave na infra {}: {}", 
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível remover a chave");
    }
}