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
    private String autorEmail;
    private String localNome;
    private LocalDateTime dataPost;
    private boolean entregue;
}