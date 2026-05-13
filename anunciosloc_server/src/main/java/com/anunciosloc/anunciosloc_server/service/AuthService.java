package com.anunciosloc.anunciosloc_server.service;


import com.anunciosloc.anunciosloc_server.kerberos.KerberosService;
import com.anunciosloc.anunciosloc_server.kerberos.KerberosTicket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

     private final KerberosService kerberosService;
    
    
    public KerberosTicket kerberosLogin(String email, String clientNonce) {
        return kerberosService.requestTicket(email, clientNonce);
    }
    
    
    public boolean authenticateRequest(String ticket, String authenticator) {
        return kerberosService.validateTicketAndAuthenticator(ticket, authenticator);
    }
    }