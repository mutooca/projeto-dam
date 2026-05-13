package com.anunciosloc.anunciosloc_server.dto;

import lombok.Data;

@Data
public class RegistoRequest {
    private String email;
    private String password;
    private String role; // "USER" / "ADMIN"
}