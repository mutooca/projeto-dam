package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AnuncioResponse {
    private UUID id;
    private String titulo;
    private String conteudo;
    private String categoria;
    private String autorEmail;
    private String estado;
    private String localNome;
    private LocalDateTime dataPost;
    private boolean entregue;
    private LocalDateTime dataPublicacao;
    private LocalDateTime visivelDe;
    private LocalDateTime visivelAte;
    private String tipoPolitica;
    private String politicaFiltro;
    private String idLocal;
    private String nomeLocal;
    // Autor simplificado
    //private String emailAutor;
    private String nomeAutor;
}