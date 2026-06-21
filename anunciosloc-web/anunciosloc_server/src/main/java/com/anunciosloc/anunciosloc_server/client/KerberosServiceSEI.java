package com.anunciosloc.anunciosloc_server.client;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://kerberos.anunciosloc.com/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface KerberosServiceSEI {
    
    @WebMethod(operationName = "RequestTicket")
    @WebResult(name = "TicketResponse")
    TicketResponse requestTicket(
        @WebParam(name = "email") String email,
        @WebParam(name = "clientNonce") String clientNonce
    );
    
    @WebMethod(operationName = "ValidateTicket")
    @WebResult(name = "ValidationResponse")
    ValidationResponse validateTicket(
        @WebParam(name = "ticket") String ticket,
        @WebParam(name = "authenticator") String authenticator
    );
    
    @WebMethod(operationName = "Logout")
    @WebResult(name = "LogoutResponse")
    LogoutResponse logout(
        @WebParam(name = "sessionId") String sessionId
    );
    
    @WebMethod(operationName = "LogoutAll")
    @WebResult(name = "LogoutResponse")
    LogoutResponse logoutAll(
        @WebParam(name = "email") String email
    );
    
    @WebMethod(operationName = "VerifySession")
    @WebResult(name = "VerifyResponse")
    VerifyResponse verifySession(
        @WebParam(name = "sessionId") String sessionId
    );
}