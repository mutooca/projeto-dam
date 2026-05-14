package com.anunciosloc.anunciosloc_server.client;

import com.anunciosloc.anunciosloc_server.config.KerberosConfig;
import jakarta.xml.ws.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.net.URL;
import java.net.URI;


@Component
public class KerberosSoapClient {
    
    @Autowired
    private KerberosConfig kerberosConfig;
    
    private KerberosServiceSEI port;
    
    private void init() {
        try {
            // Usar URI em vez de URL deprecated
            URL wsdlUrl = URI.create(kerberosConfig.getSoapUrl() + "?wsdl").toURL();
            QName serviceName = new QName("http://kerberos.anunciosloc.com/", "KerberosService");
            Service service = Service.create(wsdlUrl, serviceName);
            port = service.getPort(KerberosServiceSEI.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar com servidor Kerberos", e);
        }
    }
    
    public TicketResponse requestTicket(String email, String clientNonce) {
        if (port == null) init();
        return port.requestTicket(email, clientNonce);
    }
    
    public ValidationResponse validateTicket(String ticket, String authenticator) {
        if (port == null) init();
        return port.validateTicket(ticket, authenticator);
    }
    
    public LogoutResponse logout(String sessionId) {
        if (port == null) init();
        return port.logout(sessionId);
    }
    
    public LogoutResponse logoutAll(String email) {
        if (port == null) init();
        return port.logoutAll(email);
    }
    
    public VerifyResponse verifySession(String sessionId) {
        if (port == null) init();
        return port.verifySession(sessionId);
    }
}