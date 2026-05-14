package com.anunciosloc.kerberos.soap;

import com.anunciosloc.kerberos.service.KerberosService;
import com.anunciosloc.kerberos.soap.dto.*;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WebService(
    serviceName = "KerberosService",
    portName = "KerberosPort",
    targetNamespace = "http://kerberos.anunciosloc.com/",
    endpointInterface = "com.anunciosloc.kerberos.soap.KerberosServiceSEI"
)
@Component
public class KerberosServiceSOAPImpl implements KerberosServiceSEI {
    
    @Autowired
    private KerberosService kerberosService;
    
    @Override
    public RequestTicketResponse requestTicket(String email, String clientNonce) {
        try {
            var ticketData = kerberosService.requestTicket(email, clientNonce);
            
            RequestTicketResponse response = new RequestTicketResponse();
            response.setSuccess(true);
            response.setTicket(ticketData.ticket());
            response.setSessionKey(ticketData.sessionKey());
            response.setSessionId(ticketData.sessionId());
            response.setMessage("Ticket gerado com sucesso");
            return response;
        } catch (Exception e) {
            RequestTicketResponse response = new RequestTicketResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    
    @Override
    public ValidateTicketResponse validateTicket(String ticket, String authenticator) {
        try {
            boolean isValid = kerberosService.validateTicketAndAuthenticator(ticket, authenticator);
            
            ValidateTicketResponse response = new ValidateTicketResponse();
            response.setValid(isValid);
            response.setMessage(isValid ? "Autenticação válida" : "Autenticação inválida");
            return response;
        } catch (Exception e) {
            ValidateTicketResponse response = new ValidateTicketResponse();
            response.setValid(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    
    @Override
    public LogoutResponse logout(String sessionId) {
        try {
            kerberosService.logout(sessionId);
            
            LogoutResponse response = new LogoutResponse();
            response.setSuccess(true);
            response.setMessage("Logout realizado com sucesso");
            return response;
        } catch (Exception e) {
            LogoutResponse response = new LogoutResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    
    @Override
    public LogoutResponse logoutAll(String email) {
        try {
            kerberosService.logoutAll(email);
            
            LogoutResponse response = new LogoutResponse();
            response.setSuccess(true);
            response.setMessage("Todas as sessões foram encerradas");
            return response;
        } catch (Exception e) {
            LogoutResponse response = new LogoutResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
    
    @Override
    public VerifySessionResponse verifySession(String sessionId) {
        try {
            boolean isValid = kerberosService.isSessionValid(sessionId);
            
            VerifySessionResponse response = new VerifySessionResponse();
            response.setValid(isValid);
            response.setMessage(isValid ? "Sessão válida" : "Sessão inválida ou expirada");
            return response;
        } catch (Exception e) {
            VerifySessionResponse response = new VerifySessionResponse();
            response.setValid(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}