package com.anunciosloc.kerberos.soap;

import com.anunciosloc.kerberos.soap.dto.LogoutResponse;
import com.anunciosloc.kerberos.soap.dto.RequestTicketResponse;
import com.anunciosloc.kerberos.soap.dto.ValidateTicketResponse;
import com.anunciosloc.kerberos.soap.dto.VerifySessionResponse;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.BindingType;

@WebService(targetNamespace = "http://kerberos.anunciosloc.com/")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
@BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public interface KerberosServiceSEI {
    
    @WebMethod(operationName = "RequestTicket")
    @WebResult(name = "TicketResponse")
    RequestTicketResponse requestTicket(
        @WebParam(name = "email") String email,
        @WebParam(name = "clientNonce") String clientNonce
    );
    
    @WebMethod(operationName = "ValidateTicket")
    @WebResult(name = "ValidationResponse")
    ValidateTicketResponse validateTicket(
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
    VerifySessionResponse verifySession(
        @WebParam(name = "sessionId") String sessionId
    );
}