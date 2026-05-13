package com.anunciosloc.anunciosloc_server.dto;


import lombok.Data;

@Data
public class MensagemRequest {
    private String emailUtilizador;
    private Long infraestruturaId;
    private String titulo;
    private String conteudo;
}
