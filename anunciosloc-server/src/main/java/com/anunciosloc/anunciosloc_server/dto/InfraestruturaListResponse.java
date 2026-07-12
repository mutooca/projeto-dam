package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class InfraestruturaListResponse {
    private UUID id;
    private String nome;
    private Integer capacidade;
    private Integer bonusEntrega;
    private Integer custoPost;
    private Boolean ativa;
    private Integer totalAnuncios;
    private Integer totalEntregas;
    private Integer totalConexoes;
    private String emailGestor;
}