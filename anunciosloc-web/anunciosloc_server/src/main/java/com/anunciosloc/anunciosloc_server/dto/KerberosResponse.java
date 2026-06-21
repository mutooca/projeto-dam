package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KerberosResponse {
    private boolean success;
    private String ticket;
    private String sessionKey;
    private String sessionId;
    private String freshnessProof;
    private String message;
}