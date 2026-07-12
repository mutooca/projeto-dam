package com.anunciosloc.anunciosloc_server.client;

import lombok.Data;

@Data
public class ValidationResponse {
    private boolean valid;
    private String message;
}