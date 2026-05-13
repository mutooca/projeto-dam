package com.anunciosloc.anunciosloc_server.kerberos;

import lombok.Data;

@Data
public class CreateAuthenticatorRequest {
    private String sessionId;
    private String email;
}