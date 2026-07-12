package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InfraestruturaRegistoResponse {
    private UUID id;
    private String nome;
    private String url;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private Integer capacidade;
    private Integer bonusEntrega;
    private Integer custoPost;
    private boolean ativa;
    private LocalDateTime dataRegisto;
    private String restricoes;
    private String mensagem;
}