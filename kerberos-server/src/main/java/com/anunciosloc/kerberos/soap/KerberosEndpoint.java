package com.anunciosloc.kerberos.soap;

import com.anunciosloc.kerberos.model.Utilizador;
import com.anunciosloc.kerberos.repository.UtilizadorRepository;
import com.anunciosloc.kerberos.service.KerberosService;
import com.anunciosloc.kerberos.soap.dto.*;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class KerberosEndpoint {

    private static final String NAMESPACE = "http://kerberos.anunciosloc.com/";

    @Value("${kerberos.constante}")
    private String constante;

    @Autowired 
    private UtilizadorRepository utilizadorRepository;

    @Autowired
    private KerberosService kerberosService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "requestTicketRequest")
    @ResponsePayload
    public RequestTicketResponse requestTicket(@RequestPayload RequestTicketRequest request) {
            System.out.println("Email recebido: " + request.getEmail());
            System.out.println("ClientNonce recebido: " + request.getClientNonce());

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

    @PayloadRoot(namespace = NAMESPACE, localPart = "validateTicketRequest")
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

    @PayloadRoot(namespace = NAMESPACE, localPart = "logoutRequest")
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

    @PayloadRoot(namespace = NAMESPACE, localPart = "verifySessionRequest")
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


       @PayloadRoot(namespace = NAMESPACE, localPart = "criarUtilizadorRequest")
    @ResponsePayload
    public CriarUtilizadorResponse criarUtilizador(@RequestPayload CriarUtilizadorRequest request) {
        CriarUtilizadorResponse response = new CriarUtilizadorResponse();
        try {
            String kerberosKey = request.getPassword() + constante;
            
            Utilizador user = new Utilizador();
            user.setEmail(request.getEmail());
            user.setPalavraChave(request.getPassword());
            user.setKerberosKey(kerberosKey);
            user.setAtivo(true);
            user.setDataRegisto(LocalDateTime.now());
            utilizadorRepository.save(user);
            
            response.setSuccess(true);
            response.setMessage("Utilizador criado no Kerberos com sucesso");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Erro ao criar utilizador no Kerberos: " + e.getMessage());
        }
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "verificarUtilizadorRequest")
    @ResponsePayload
    public VerificarUtilizadorResponse verificarUtilizador(@RequestPayload VerificarUtilizadorRequest request) {
        VerificarUtilizadorResponse response = new VerificarUtilizadorResponse();
        try {
            boolean existe = utilizadorRepository.existsByEmail(request.getEmail());
            response.setExiste(existe);
            response.setMessage(existe ? "Utilizador existe" : "Utilizador não encontrado");
        } catch (Exception e) {
            response.setExiste(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    
}