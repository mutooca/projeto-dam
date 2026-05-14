package com.anunciosloc.kerberos.soap;

import com.anunciosloc.kerberos.service.KerberosService;
import com.anunciosloc.kerberos.soap.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class KerberosEndpoint {
    
    private static final String NAMESPACE = "http://kerberos.anunciosloc.com/";
    
    @Autowired
    private KerberosService kerberosService;
    
    @PayloadRoot(namespace = NAMESPACE, localPart = "RequestTicketRequest")
    @ResponsePayload
    public RequestTicketResponse requestTicket(@RequestPayload RequestTicketRequest request) {
        RequestTicketResponse response = new RequestTicketResponse();
        try {
            var ticketData = kerberosService.requestTicket(request.getEmail(), request.getClientNonce());
            response.setSuccess(true);
            response.setTicket(ticketData.ticket());
            response.setSessionKey(ticketData.sessionKey());
            response.setSessionId(ticketData.sessionId());
            response.setMessage("Ticket gerado com sucesso");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE, localPart = "ValidateTicketRequest")
    @ResponsePayload
    public ValidateTicketResponse validateTicket(@RequestPayload ValidateTicketRequest request) {
        ValidateTicketResponse response = new ValidateTicketResponse();
        try {
            boolean isValid = kerberosService.validateTicketAndAuthenticator(
                request.getTicket(), 
                request.getAuthenticator()
            );
            response.setValid(isValid);
            response.setMessage(isValid ? "Autenticação válida" : "Autenticação inválida");
        } catch (Exception e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE, localPart = "LogoutRequest")
    @ResponsePayload
    public LogoutResponse logout(@RequestPayload LogoutRequest request) {
        LogoutResponse response = new LogoutResponse();
        try {
            kerberosService.logout(request.getSessionId());
            response.setSuccess(true);
            response.setMessage("Logout realizado com sucesso");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE, localPart = "VerifySessionRequest")
    @ResponsePayload
    public VerifySessionResponse verifySession(@RequestPayload VerifySessionRequest request) {
        VerifySessionResponse response = new VerifySessionResponse();
        try {
            boolean isValid = kerberosService.isSessionValid(request.getSessionId());
            response.setValid(isValid);
            response.setMessage(isValid ? "Sessão válida" : "Sessão inválida ou expirada");
        } catch (Exception e) {
            response.setValid(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}