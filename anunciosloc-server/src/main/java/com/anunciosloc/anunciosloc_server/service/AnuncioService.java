package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.PerfilItem;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.dto.AnuncioInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.PerfilItemSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ReceberAnunciosRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ReceberAnunciosResponse;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnuncioService {

    private final InfrastruturaSoapClient soapClient;
    private final UtilizadorRepository utilizadorRepository;
    private final PerfilService perfilService;

    public String postarAnuncio(PostarAnuncioRequestSOAP request) {
        log.info(" [ANUNCIOSLOC] Postando anúncio: {}", request.getTitulo());
        log.info("   Autor: {}", request.getEmailAutor());
        log.info("   Local remoto: {}", request.getIdLocal());
        log.info("   Tipo politica: {}", request.getTipoPolitica());
        log.info("   Politica filtro: {}", request.getPoliticaFiltro());
        log.info("   Visivel de: {}", request.getVisivelDe());
        log.info("   Visivel ate: {}", request.getVisivelAte());

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        String ultimoErro = "Nenhuma infraestrutura disponível";
        for (InfraProxy infra : infras) {
            PostarAnuncioResponseSOAP resposta = infra.postarAnuncio(request);
            if (resposta != null && resposta.isSucesso()) {
                log.info(" Anúncio postado na infra: {} (idAnuncio={})", infra.getServiceUrl(), resposta.getIdAnuncio());
                return resposta.getMensagem() != null && !resposta.getMensagem().isBlank()
                        ? resposta.getMensagem()
                        : "Anúncio publicado com sucesso";
            }
            ultimoErro = resposta != null && resposta.getMensagem() != null
                    ? resposta.getMensagem()
                    : "Resposta inválida da infraestrutura";
            log.warn(" Falha ao postar anúncio na infra {}: {}", infra.getServiceUrl(), ultimoErro);
        }

        throw new RuntimeException(ultimoErro);
    }

    public List<AnuncioInfoSOAP> receberAnuncios(String email, String localId) {
        log.info(" [ANUNCIOSLOC] Recebendo anúncios para: {} no local: {}", email, localId);

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            log.warn(" Nenhuma infraestrutura disponível");
            return List.of();
        }

        // O perfil é agora armazenado apenas no anunciosloc-server; a infra já não tem BD
        // própria para isto, por isso enviamo-lo junto com o pedido para ela poder aplicar
        // a política whitelist/blacklist na entrega.
        List<PerfilItemSOAP> perfilParaEntrega = perfilService.obterPerfilParaEntrega(email).stream()
                .map(item -> PerfilItemSOAP.builder().chave(item.getChave()).valor(item.getValor()).build())
                .collect(Collectors.toList());
        log.info("   Perfil do destinatário para aplicar política: {} atributo(s)", perfilParaEntrega.size());

        // Para cada infraestrutura, tenta buscar os anúncios
        for (InfraProxy infra : infras) {
            try {
                ReceberAnunciosRequestSOAP request = ReceberAnunciosRequestSOAP.builder()
                        .email(email)
                        .idLocal(localId)
                        .perfil(perfilParaEntrega)
                        .build();

                log.info("   A consultar infra {} com {} atributo(s) do perfil do destinatario",
                        infra.getServiceUrl(),
                        perfilParaEntrega.size());

                ReceberAnunciosResponse response = infra.receberAnuncios(request);
                if (response == null) {
                    log.warn("   Infra {} devolveu resposta nula ao receber anuncios", infra.getServiceUrl());
                    continue;
                }

                if (!response.isSucesso()) {
                    log.warn("   Infra {} devolveu sucesso=false: {}",
                            infra.getServiceUrl(),
                            response.getMensagem());
                    continue;
                }

                List<AnuncioInfoSOAP> anuncios = response.getAnuncios() != null
                        ? response.getAnuncios()
                        : List.of();

                if (anuncios != null && !anuncios.isEmpty()) {
                    log.info(" {} anúncios encontrados na infra: {}",
                            anuncios.size(), infra.getServiceUrl());
                    return anuncios;
                }

                log.info("   Infra {} nao devolveu anuncios elegiveis para {}", infra.getServiceUrl(), email);
            } catch (Exception e) {
                log.warn(" Falha ao buscar anúncios na infra {}: {}",
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        log.info(" Nenhum anúncio encontrado para o local: {}", localId);
        return List.of();
    }

    public String eliminarAnuncio(String idAnuncio, String emailUtilizador) {
        String role = resolverRole(emailUtilizador);
        log.info(" [ANUNCIOSLOC] Eliminando anúncio: {} por {} (role resolvida no servidor: {})",
                idAnuncio, emailUtilizador, role);

        List<InfraProxy> infras = soapClient.obterClientes();
        if (infras.isEmpty()) {
            throw new RuntimeException("Nenhuma infraestrutura disponível");
        }

        for (InfraProxy infra : infras) {
            try {
                MensagemResponse response = infra.eliminarAnuncio(idAnuncio, emailUtilizador, role);
                if (response != null && response.isSucesso()) {
                    log.info(" Anúncio eliminado na infra: {}", infra.getServiceUrl());
                    return response.getMensagem();
                }
            } catch (Exception e) {
                log.warn(" Falha ao eliminar anúncio na infra {}: {}",
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        throw new RuntimeException("Não foi possível eliminar o anúncio");
    }

    public List<AnuncioInfoSOAP> listarMeusAnuncios(String email) {
        log.info(" [ANUNCIOSLOC] Listando meus anúncios: {}", email);

        List<InfraProxy> infras = soapClient.obterClientes();

        if (infras.isEmpty()) {
            log.warn(" Nenhuma infraestrutura disponível");
            return List.of();
        }

        log.info(" {} infraestruturas disponíveis", infras.size());

        List<AnuncioInfoSOAP> todosAnuncios = new ArrayList<>();

        for (InfraProxy infra : infras) {
            try {
                log.info(" Buscando anúncios na infra: {}", infra.getServiceUrl());

                List<AnuncioInfoSOAP> anuncios = infra.listarAnunciosPorEmail(email);

                if (anuncios != null && !anuncios.isEmpty()) {
                    todosAnuncios.addAll(anuncios);
                    log.info("  {} anúncios encontrados na infra", anuncios.size());
                } else {
                    log.info("  Nenhum anúncio encontrado na infra");
                }

            } catch (Exception e) {
                log.warn("  Falha ao buscar anúncios na infra {}: {}",
                        infra.getServiceUrl(), e.getMessage());
            }
        }

        log.info(" Total de anúncios encontrados: {}", todosAnuncios.size());

        // ordenar por data de post mais recente, sao first
        todosAnuncios.sort((a1, a2) -> {
            if (a1.getDataPublicacao() == null)
                return 1;
            if (a2.getDataPublicacao() == null)
                return -1;
            return a2.getDataPublicacao().compareTo(a1.getDataPublicacao());
        });

        return todosAnuncios;
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

    private String resolverRole(String emailUtilizador) {
        return utilizadorRepository.findByEmail(emailUtilizador)
                .map(Utilizador::getRole)
                .orElseGet(() -> {
                    log.warn(" [ANUNCIOSLOC] Utilizador autenticado '{}' não encontrado na BD local. A assumir role USER.",
                            emailUtilizador);
                    return "USER";
                });
    }

}
