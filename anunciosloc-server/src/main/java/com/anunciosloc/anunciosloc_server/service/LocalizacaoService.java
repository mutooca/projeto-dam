package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.LocalizacaoRequest;
import com.anunciosloc.anunciosloc_server.dto.LocalizacaoResponse;
import com.anunciosloc.anunciosloc_server.dto.PerfilItem; 
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalizacaoService {

    private final UtilizadorRepository utilizadorRepository;
    private final InfrastruturaSoapClient soapClient;
    private final PerfilService perfilService;

    /**
     * Recebe localização e perfil do utilizador
     * Chamado automaticamente pela APP a cada 30s
     */
    @Transactional
    public LocalizacaoResponse receberLocalizacao(LocalizacaoRequest request) {
        log.info(" [LOCALIZACAO] Recebendo localização de: {}", request.getEmail());
        log.info("   Lat: {}, Lon: {}", request.getLatitude(), request.getLongitude());
        log.info("   Perfil: {} itens", request.getPerfil() != null ? request.getPerfil().size() : 0);

        try {
            
            Utilizador user = utilizadorRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

            user.setUltimaLocalizacao(request.getLatitude() + "," + request.getLongitude());
            user.setUltimaAtualizacaoLocalizacao(LocalDateTime.now());
            utilizadorRepository.save(user);

            log.info("  Localização atualizada na BD");

            
            if (request.getPerfil() != null && !request.getPerfil().isEmpty()) {
                enviarPerfilParaInfra(request.getEmail(), request.getPerfil());
            }

            //enviarLocalizacaoParaInfra(request.getEmail(), request.getLatitude(), request.getLongitude());

            
            //int locaisProximos = contarLocaisProximos(request.getLatitude(), request.getLongitude());

            log.info("   Localização processada com sucesso!");

            return LocalizacaoResponse.builder()
                    .sucesso(true)
                    .mensagem("Localização atualizada com sucesso")
                    .email(request.getEmail())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    //.locaisProximos(locaisProximos)
                    .build();

        } catch (Exception e) {
            log.error(" Erro ao processar localização: {}", e.getMessage());
            return LocalizacaoResponse.builder()
                    .sucesso(false)
                    .mensagem("Erro: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Envia o perfil para o Infra-Server via SOAP
     */
    private void enviarPerfilParaInfra(String email, List<PerfilItem> perfil) {
        log.info("   Enviando perfil para Infra-Server...");

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            log.warn("   Nenhuma infraestrutura disponível para enviar perfil");
            return;
        }

        for (InfraProxy proxy : infras) {
            try {
                MensagemResponse response = proxy.adicionarPerfil(email, perfil);
                if (response != null && response.isSucesso()) {
                    log.info("  Perfil enviado para infra: {}", proxy.getServiceUrl());
                    return;
                }
            } catch (Exception e) {
                log.warn("   Falha ao enviar perfil para infra: {}", e.getMessage());
            }
        }
    }

    /**
     * Envia a localização para o Infra-Server (para rastreamento)
     
    private void enviarLocalizacaoParaInfra(String email, Double lat, Double lon) {
        // Implementar se necessário
        // Ex: proxy.atualizarLocalizacao(email, lat, lon)
    }*/

    /**
     * Conta quantos locais estão próximos da localização
     
    private int contarLocaisProximos(Double lat, Double lon) {
        // Implementar consulta ao Infra-Server
        // Retorna o número de locais dentro do raio
        return 0;  // Placeholder
    }*/
}