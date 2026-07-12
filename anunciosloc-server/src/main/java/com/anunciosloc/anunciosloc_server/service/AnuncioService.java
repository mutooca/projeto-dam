package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.uddi.dto.AnuncioInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ReceberAnunciosRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnuncioService {

    private final InfrastruturaSoapClient soapClient;

    public String postarAnuncio(PostarAnuncioRequestSOAP request) {
        log.info(" [ANUNCIOSLOC] Postando anúncio: {}", request.getTitulo());

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        InfraProxy infra = infras.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhuma infra disponível"));

        String resultado = infra.postarAnuncio(request);
        log.info(" Anúncio postado na infra: {}", infra.getServiceUrl());
        return resultado;
    }

    public List<AnuncioInfoSOAP> receberAnuncios(String email, String localId) {
        log.info(" [ANUNCIOSLOC] Recebendo anúncios para: {} no local: {}", email, localId);

        
        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            log.warn(" Nenhuma infraestrutura disponível");
            return List.of();
        }

        //  Para cada infraestrutura, tenta buscar os anúncios
        for (InfraProxy infra : infras) {
            try {
                ReceberAnunciosRequestSOAP request = ReceberAnunciosRequestSOAP.builder()
                        .email(email)
                        .idLocal(localId)
                        .build();

                List<AnuncioInfoSOAP> anuncios = soapClient.receberAnunciosList(request);
                
                if (anuncios != null && !anuncios.isEmpty()) {
                    log.info(" {} anúncios encontrados na infra: {}", 
                            anuncios.size(), infra.getServiceUrl());
                    return anuncios;
                }
            } catch (Exception e) {
                log.warn(" Falha ao buscar anúncios na infra {}: {}", 
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        log.info(" Nenhum anúncio encontrado para o local: {}", localId);
        return List.of();
    }

    public MensagemResponse marcarComoLido(String idAnuncio, String emailUtilizador) {
        log.info(" [ANUNCIOSLOC] Marcando anúncio como lido");

       
        List<InfraProxy> infras = soapClient.obterClientes();
        
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        
        for (InfraProxy infra : infras) {
            try {
                MensagemResponse resultado = infra.marcarComoLido(idAnuncio, emailUtilizador);
                log.info(" Anúncio marcado como lido na infra: {}", 
                         infra.getServiceUrl());
                return resultado;
            } catch (Exception e) {
                log.warn("  Falha ao marcar como lido na infra {}: {}", 
                         infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível marcar o anúncio como lido");
    }

}