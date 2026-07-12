package com.anunciosloc.anunciosloc_server.client;

import lombok.Data;

@Data
public class LogoutResponse {
    private boolean success;
    private String message;
}
