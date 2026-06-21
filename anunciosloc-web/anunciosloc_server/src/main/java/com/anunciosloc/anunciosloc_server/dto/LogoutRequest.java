package com.anunciosloc.anunciosloc_server.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String sessionId;
}