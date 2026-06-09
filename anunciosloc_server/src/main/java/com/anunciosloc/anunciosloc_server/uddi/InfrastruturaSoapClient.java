package com.anunciosloc.anunciosloc_server.uddi;

import com.anunciosloc.anunciosloc_server.uddi.dto.*;
import com.anunciosloc.anunciosloc_server.uddi.InfrastructureServiceSEI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastruturaSoapClient {

    private final UddiClient uddiClient;

    private static final String NAMESPACE =
            "http://infrastructura.anunciosloc.uan.com";
    private static final QName SERVICE_QNAME =
            new QName(NAMESPACE, "InfrastructureService");
    private static final QName PORT_QNAME =
            new QName(NAMESPACE, "InfrastructurePort");

   

    
    public List<InfraProxy> obterClientes() {
        List<String> urls = uddiClient.obterUrlsInfraestruturas();
        List<InfraProxy> clientes = new ArrayList<>();

        for (String serviceUrl : urls) {
            try {
                clientes.add(criarProxy(serviceUrl));
                log.debug("Cliente SOAP criado para: {}", serviceUrl);
            } catch (Exception e) {
                log.warn("Não foi possível criar cliente para {}: {}",
                         serviceUrl, e.getMessage());
            }
        }
        return clientes;
    }

    
    public InfraProxy obterClientePorNome(String serviceName) {
        String url = uddiClient.obterUrlInfraestrutura(serviceName);
        if (url == null) return null;
        try {
            return criarProxy(url);
        } catch (Exception e) {
            log.error("Erro ao criar cliente para '{}': {}",
                      serviceName, e.getMessage());
            return null;
        }
    }

    
    private InfraProxy criarProxy(String serviceUrl) throws Exception {
    URL wsdlUrl = URI.create(serviceUrl + "?wsdl").toURL();
    Service service = Service.create(wsdlUrl, SERVICE_QNAME);
    

    InfrastructureServiceSEI port = service.getPort(
            PORT_QNAME, InfrastructureServiceSEI.class);

    return new InfraProxy() {
        public String getServiceUrl() { return serviceUrl; }

        public InfraInfoResponse obterInfoInfraestrutura() {
            var r = port.obterInfoInfraestrutura();
            InfraInfoResponse resp = new InfraInfoResponse();
            resp.setNome(r.getNome());
            resp.setCapacidade(r.getCapacidade());
            resp.setBonusEntrega(r.getBonusEntrega());
            resp.setCustoPost(r.getCustoPost());
            resp.setTotalAnuncios(r.getTotalAnuncios());
            resp.setTotalEntregas(r.getTotalEntregas());
            resp.setTotalConexoes(r.getTotalConexoes());
            resp.setConectadosAgora(r.getConectadosAgora());
            resp.setConexoesDisponiveis(r.getConexoesDisponiveis());
            return resp;
        }

        public LerSaldoResponse lerSaldo(String idUtilizador) {
            var r = port.lerSaldo(idUtilizador);
            LerSaldoResponse resp = new LerSaldoResponse();
            resp.setIdUtilizador(r.getIdUtilizador());
            resp.setSaldo(r.getSaldo());
            resp.setVersao(r.getVersao());
            resp.setEncontrado(r.isEncontrado());
            return resp;
        }

        public EscreverSaldoResponse escreverSaldo(String idUtilizador,
                                                    float novoSaldo,
                                                    int versao) {
            var r = port.escreverSaldo(idUtilizador, novoSaldo, versao);
            EscreverSaldoResponse resp = new EscreverSaldoResponse();
            resp.setSucesso(r.isSucesso());
            resp.setVersaoActual(r.getVersaoActual());
            resp.setMensagem(r.getMensagem());
            return resp;
        }

        public String ping() { return port.ping(); }
    };
}
}