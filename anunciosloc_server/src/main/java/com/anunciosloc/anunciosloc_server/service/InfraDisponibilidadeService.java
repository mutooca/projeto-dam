package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraDisponibilidadeService {

    private final InfrastruturaSoapClient soapClient;
    public InfraProxy verificarDisponibilidade(String infraNome) {

        InfraProxy proxy = soapClient.obterClientePorNome(infraNome);

        if (proxy == null) {
            log.error("Infraestrutura '{}' não encontrada no UDDI", infraNome);
            throw new RuntimeException(
                "Infraestrutura '" + infraNome + "' não está registada no UDDI. " +
                "O servidor de infraestrutura pode não estar a correr.");
        }
        try {
            String pong = proxy.ping();
            if (pong == null || !pong.contains("PONG")) {
                throw new RuntimeException("Resposta inválida ao ping");
            }
            log.debug("Infraestrutura '{}' disponível: {}", infraNome, pong);
        } catch (Exception e) {
            log.error("Infraestrutura '{}' não respondeu ao ping: {}",
                      infraNome, e.getMessage());
            throw new RuntimeException(
                "Infraestrutura '" + infraNome + "' não está a responder. " +
                "Tente novamente mais tarde.");
        }

        return proxy;
    }

    /*
     para operaçoes onde o fallback eh aceitavel
     */
    public boolean estaDisponivel(String infraNome) {
        try {
            verificarDisponibilidade(infraNome);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}