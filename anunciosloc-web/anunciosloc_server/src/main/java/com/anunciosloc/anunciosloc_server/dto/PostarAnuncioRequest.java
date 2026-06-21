package com.anunciosloc.anunciosloc_server.dto;


import java.util.UUID;

import lombok.Data;

@Data
public class PostarAnuncioRequest {
    private String emailAutor;
    private UUID localId;
    private String titulo;
    private String conteudo;
    private String categoria;
}
