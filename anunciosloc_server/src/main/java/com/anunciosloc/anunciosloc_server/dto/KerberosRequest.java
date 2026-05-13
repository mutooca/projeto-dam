package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KerberosRequest {
    private String email;
    private String clientNonce;
    private String ticket;
    private String authenticator;
    private String sessionId;
}