package com.anunciosloc.anunciosloc_server.uddi;

import com.anunciosloc.anunciosloc_server.uddi.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.MessageContext;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPMessage;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastruturaSoapClient {

    private final UddiClient uddiClient;

    private static final String NAMESPACE = "http://infrastructura.anunciosloc.uan.com";
    private static final QName SERVICE_QNAME = new QName(NAMESPACE, "InfrastructureService");
    private static final QName PORT_QNAME = new QName(NAMESPACE, "InfrastructurePort");

    public List<InfraProxy> obterClientes() {
        List<String> urls = uddiClient.obterUrlsInfraestruturas();
        List<InfraProxy> clientes = new ArrayList<>();

        for (String serviceUrl : urls) {
            try {
                clientes.add(criarProxy(serviceUrl));
                log.debug("Cliente SOAP criado para: {}", serviceUrl);
            } catch (Exception e) {
                log.warn("Não foi possível criar cliente para {}: {}", serviceUrl, e.getMessage());
            }
        }
        return clientes;
    }

    public InfraProxy obterClientePorNome(String serviceName) {
        String url = uddiClient.obterUrlInfraestrutura(serviceName);
        if (url == null)
            return null;
        try {
            return criarProxy(url);
        } catch (Exception e) {
            log.error("Erro ao criar cliente para '{}': {}", serviceName, e.getMessage());
            return null;
        }
    }

    public List<AnuncioInfoSOAP> receberAnunciosList(ReceberAnunciosRequestSOAP request) {
        log.info(" SOAP: receberAnunciosList - email={}, idLocal={}",
                request.getEmail(), request.getIdLocal());

        List<InfraProxy> infras = obterClientes();
        if (infras.isEmpty()) {
            log.warn("Nenhuma infraestrutura disponível");
            return List.of();
        }

        InfraProxy infra = infras.get(0);

        ReceberAnunciosResponse response = infra.receberAnuncios(request);

        if (response != null && response.isSucesso()) {
            List<AnuncioInfoSOAP> anuncios = response.getAnuncios();
            log.info(" Resposta SOAP recebida: {} anúncios",
                    anuncios != null ? anuncios.size() : 0);
            return anuncios != null ? anuncios : List.of();
        } else {
            String mensagem = response != null ? response.getMensagem() : "Resposta nula";
            log.warn(" Resposta SOAP sem sucesso: {}", mensagem);
            return List.of();
        }
    }

    public InfraProxy criarProxy(String serviceUrl) throws Exception {
        URL wsdlUrl = URI.create(serviceUrl + "?wsdl").toURL();

        Service service = Service.create(wsdlUrl, SERVICE_QNAME);

        service.setHandlerResolver(new HandlerResolver() {
            @Override
            @SuppressWarnings("rawtypes")
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList<>();
                handlers.add(new SOAPHandler<SOAPMessageContext>() {
                    @Override
                    public boolean handleMessage(SOAPMessageContext context) {
                        Boolean outbound = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
                        try {
                            if (!outbound) {
                                // Log da resposta SOAP
                                SOAPMessage message = context.getMessage();
                                if (message != null) {
                                    String xml = message.getSOAPPart().getEnvelope().getBody()
                                            .getFirstChild().toString();
                                    log.info("SOAP Response XML: {}", xml);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Erro ao logar SOAP", e);
                        }
                        return true;
                    }

                    @Override
                    public boolean handleFault(SOAPMessageContext context) {
                        return true;
                    }

                    @Override
                    public void close(MessageContext context) {

                    }

                    @Override
                    public Set<QName> getHeaders() {
                        return null;
                    }
                });
                return handlers;
            }
        });

        InfrastructureServiceSEI port = service.getPort(PORT_QNAME, InfrastructureServiceSEI.class);

        return new InfraProxy() {
            public String getServiceUrl() {
                return serviceUrl;
            }

            public InfraInfoResponse obterInfoInfraestrutura() {
                return port.obterInfoInfraestrutura();
            }

            public String ping() {
                try {
                    log.info(" SOAP: Enviando ping para {}", serviceUrl);
                    PingResponse response = port.ping();

                    if (response == null) {
                        log.warn(" SOAP: Resposta do ping é null");
                        return "ERROR: null response";
                    }

                    String resultado = response.getMensagem();
                    log.info(" SOAP: Resposta ping recebida: '{}'", resultado);
                    return resultado;

                } catch (Exception e) {
                    log.error(" SOAP: Erro no ping: {}", e.getMessage());
                    return "ERROR: " + e.getMessage();
                }
            }

            public LerSaldoResponse lerSaldo(String email) {
                return port.lerSaldo(email);
            }

            public EscreverSaldoResponse escreverSaldo(String email, float novoSaldo, int versao) {
                return port.escreverSaldo(email, novoSaldo, versao);
            }

            public ObterSaldoResponse obterSaldo(String email) {
                return port.obterSaldo(email);
            }

            public String criarLocal(CriarLocalRequestSOAP request) {
                return port.criarLocal(request);
            }

            public ListarLocaisResponse listarLocais(Double lat, Double lon) {
                log.info(" SOAP: listarLocais - lat={}, lon={}", lat, lon);
                try {

                    log.info(" SOAP: Chamando port.listarLocais...");
                    ListarLocaisResponse response = port.listarLocais(lat, lon);

                    if (response != null) {
                        log.info(" Resposta SOAP: sucesso={}, mensagem={}, locais={}",
                                response.isSucesso(),
                                response.getMensagem(),
                                response.getLocais() != null ? response.getLocais().size() : 0);

                        if (response.getLocais() != null) {
                            for (LocalInfoSOAP local : response.getLocais()) {
                                log.info("   Local: id={}, nome={}, distância={}m",
                                        local.getIdLocal(),
                                        local.getNome(),
                                        local.getDistancia() != null ? Math.round(local.getDistancia()) : "?");
                            }
                        }
                    }

                    return response;
                } catch (Exception e) {
                    log.error("Erro ao listar locais: {}", e.getMessage(), e);
                    return ListarLocaisResponse.builder()
                            .sucesso(false)
                            .mensagem("Erro: " + e.getMessage())
                            .locais(List.of())
                            .build();
                }
            }

            public String postarAnuncio(PostarAnuncioRequestSOAP request) {
                return port.postarAnuncio(request);
            }

            public ReceberAnunciosResponse receberAnuncios(ReceberAnunciosRequestSOAP request) {
                log.info(" SOAP: receberAnuncios - email={}, idLocal={}",
                        request.getEmail(), request.getIdLocal());

                try {
                    // Log do request
                    log.debug("Request: email={}, idLocal={}",
                            request.getEmail(), request.getIdLocal());

                    ReceberAnunciosResponse response = port.receberAnuncios(request);

                    // Log detalhado da response
                    if (response != null) {
                        log.info(" Resposta SOAP recebida: sucesso={}, mensagem={}, anúncios={}",
                                response.isSucesso(),
                                response.getMensagem(),
                                response.getAnuncios() != null ? response.getAnuncios().size() : 0);

                        if (response.getAnuncios() != null && !response.getAnuncios().isEmpty()) {
                            for (AnuncioInfoSOAP a : response.getAnuncios()) {
                                log.info("   - ID: {}, Título: {}, Autor: {}, Local: {}",
                                        a.getId(), a.getTitulo(), a.getAutorEmail(), a.getNomeLocal());
                            }
                        }
                    } else {
                        log.warn(" Resposta SOAP é null");
                    }

                    return response;

                } catch (Exception e) {
                    log.error("Erro ao chamar SOAP: {}", e.getMessage(), e);
                    return ReceberAnunciosResponse.builder()
                            .sucesso(false)
                            .mensagem("Erro: " + e.getMessage())
                            .anuncios(List.of())
                            .build();
                }
            }

            @Override
            public List<AnuncioInfoSOAP> listarAnunciosPorEmail(String email) {
                log.info(" SOAP: listarAnunciosPorEmail - email={}", email);
                try {
                    List<AnuncioInfoSOAP> response = port.listarAnunciosPorEmail(email);

                    if (response != null) {
                        log.info(" Resposta SOAP: {} anúncios encontrados", response.size());
                        for (AnuncioInfoSOAP a : response) {
                            log.info("   - ID: {}, Título: {}, Local: {}, Estado: {}",
                                    a.getId(),
                                    a.getTitulo(),
                                    a.getNomeLocal(),
                                    a.getEstado());
                        }
                    } else {
                        log.warn(" Resposta SOAP é null");
                    }

                    return response != null ? response : List.of();

                } catch (Exception e) {
                    log.error(" Erro ao listar anúncios por email: {}", e.getMessage());
                    return List.of();
                }
            }

            @Override
            public MensagemResponse marcarComoLido(String idAnuncio, String emailUtilizador) {
                log.info(" SOAP: marcarComoLido - idAnuncio={}, email={}",
                        idAnuncio, emailUtilizador);

                try {
                    MensagemResponse response = port.marcarComoLido(idAnuncio, emailUtilizador);

                    if (response != null) {
                        log.info(" Resposta SOAP: sucesso={}, mensagem={}",
                                response.isSucesso(),
                                response.getMensagem());
                    }

                    return response;
                } catch (Exception e) {
                    log.error("  Erro ao marcar como lido: {}", e.getMessage());
                    return MensagemResponse.builder()
                            .sucesso(false)
                            .mensagem("Erro: " + e.getMessage())
                            .build();
                }
            }

        };
    }

}