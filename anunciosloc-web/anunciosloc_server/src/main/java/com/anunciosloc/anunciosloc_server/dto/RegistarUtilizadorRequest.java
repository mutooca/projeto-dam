package com.anunciosloc.anunciosloc_server.dto;

import lombok.Data;

@Data
public class RegistarUtilizadorRequest {
    private String nome;
    private String email;
    private String palavraChave;
    private String role; 
    private String preferenciaAnuncio;
}