package com.anunciosloc.anunciosloc_server.client;

import jakarta.xml.soap.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import java.io.ByteArrayOutputStream;

@Component
public class KerberosSoapClient {

    @Value("${kerberos.soap.url:http://localhost:8085/ws/kerberos}")
    private String kerberosUrl;

    public String requestTicket(String email, String clientNonce) throws Exception {
        // Criar mensagem SOAP
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // Criar envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("kerb", "http://kerberos.anunciosloc.com/");

        // Criar body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement requestTicket = soapBody.addChildElement("requestTicketRequest", "kerb");
        
        SOAPElement emailElement = requestTicket.addChildElement("email", "kerb");
        emailElement.addTextNode(email);
        
        SOAPElement nonceElement = requestTicket.addChildElement("clientNonce", "kerb");
        nonceElement.addTextNode(clientNonce);

        // Salvar mensagem
        soapMessage.saveChanges();

        // Enviar e receber resposta
        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        
        SOAPMessage response = connection.call(soapMessage, kerberosUrl);
        connection.close();

        // Extrair resposta
        return extractResponse(response);
    }

     public String validateTicket(String ticket, String authenticator) throws Exception {
        System.out.println(" KerberosSoapClient.validateTicket");
        System.out.println("   Ticket: " + ticket);
        System.out.println("   Authenticator: " + authenticator);
        
        
        
        
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("kerb", "http://kerberos.anunciosloc.com/");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement validateTicket = soapBody.addChildElement("validateTicketRequest", "kerb");
        
        SOAPElement ticketElement = validateTicket.addChildElement("ticket", "kerb");
        ticketElement.addTextNode(ticket);
        
        SOAPElement authElement = validateTicket.addChildElement("authenticator", "kerb");
        authElement.addTextNode(authenticator);

        soapMessage.saveChanges();

        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        
        SOAPMessage response = connection.call(soapMessage, kerberosUrl);
        connection.close();

        return extractValidationResponse(response);
    }

    public String logout(String sessionId) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("kerb", "http://kerberos.anunciosloc.com/");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement logoutRequest = soapBody.addChildElement("logoutRequest", "kerb");
        
        SOAPElement sessionElement = logoutRequest.addChildElement("sessionId", "kerb");
        sessionElement.addTextNode(sessionId);

        soapMessage.saveChanges();

        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        
        SOAPMessage response = connection.call(soapMessage, kerberosUrl);
        connection.close();

        return extractLogoutResponse(response);
    }

    private String extractResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        
        // Procurar por ticket
        SOAPElement requestTicketResponse = (SOAPElement) body.getChildElements().next();
        
        String success = getElementValue(requestTicketResponse, "success");
        String ticket = getElementValue(requestTicketResponse, "ticket");
        String sessionKey = getElementValue(requestTicketResponse, "sessionKey");
        String sessionId = getElementValue(requestTicketResponse, "sessionId");
        String message = getElementValue(requestTicketResponse, "message");
        
        return String.format("{\"success\":%s,\"ticket\":\"%s\",\"sessionKey\":\"%s\",\"sessionId\":\"%s\",\"message\":\"%s\"}",
                success, ticket, sessionKey, sessionId, message);
    }

    private String extractValidationResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        SOAPElement validateResponse = (SOAPElement) body.getChildElements().next();
        
        String valid = getElementValue(validateResponse, "valid");
        String message = getElementValue(validateResponse, "message");
        
        return String.format("{\"valid\":%s,\"message\":\"%s\"}", valid, message);
    }

    private String extractLogoutResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        SOAPElement logoutResponse = (SOAPElement) body.getChildElements().next();
        
        String success = getElementValue(logoutResponse, "success");
        String message = getElementValue(logoutResponse, "message");
        
        return String.format("{\"success\":%s,\"message\":\"%s\"}", success, message);
    }

    private String getElementValue(SOAPElement parent, String tagName) {
        java.util.Iterator<?> it = parent.getChildElements();
        while (it.hasNext()) {
            SOAPElement element = (SOAPElement) it.next();
            if (element.getLocalName().equals(tagName)) {
                return element.getValue();
            }
        }
        return "";
    }

    public String criarUtilizador(String email, String password) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("kerb", "http://kerberos.anunciosloc.com/");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement criarUtilizador = soapBody.addChildElement("criarUtilizadorRequest", "kerb");
        
        SOAPElement emailElement = criarUtilizador.addChildElement("email", "kerb");
        emailElement.addTextNode(email);
        
        SOAPElement passwordElement = criarUtilizador.addChildElement("password", "kerb");
        passwordElement.addTextNode(password);

        soapMessage.saveChanges();

        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        
        SOAPMessage response = connection.call(soapMessage, kerberosUrl);
        connection.close();

        return extractCriarUtilizadorResponse(response);
    }

    private String extractCriarUtilizadorResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        SOAPElement criarUtilizadorResponse = (SOAPElement) body.getChildElements().next();
        
        String success = getElementValue(criarUtilizadorResponse, "success");
        String message = getElementValue(criarUtilizadorResponse, "message");
        
        return String.format("{\"success\":%s,\"message\":\"%s\"}", success, message);
    }

    public boolean verificarUtilizador(String email) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("kerb", "http://kerberos.anunciosloc.com/");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement verificarUtilizador = soapBody.addChildElement("verificarUtilizadorRequest", "kerb");
        
        SOAPElement emailElement = verificarUtilizador.addChildElement("email", "kerb");
        emailElement.addTextNode(email);

        soapMessage.saveChanges();

        SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = connectionFactory.createConnection();
        
        SOAPMessage response = connection.call(soapMessage, kerberosUrl);
        connection.close();

        return extractVerificarUtilizadorResponse(response);
    }

    private boolean extractVerificarUtilizadorResponse(SOAPMessage response) throws Exception {
        SOAPBody body = response.getSOAPBody();
        SOAPElement verificarResponse = (SOAPElement) body.getChildElements().next();
        
        String existe = getElementValue(verificarResponse, "existe");
        return Boolean.parseBoolean(existe);
    }
    
}