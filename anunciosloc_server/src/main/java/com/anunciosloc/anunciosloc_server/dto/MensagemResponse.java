package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MensagemResponse {
    private Long id;
    private String titulo;
    private String conteudo;
    private String autorEmail;
    private String localNome;
    private LocalDateTime dataPost;
    private boolean entregue;
}