package com.anunciosloc.anunciosloc_server.dto.admin;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfraestruturaAdminDTO {
    private UUID id;
    private String nome;
    private String url;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private Integer capacidade;
    private Integer bonusEntrega;
    private Integer custoPost;
    private Boolean ativa;
    private Boolean online;
    private Boolean registadaUDDI;
    private Integer conexoesAtuais;
}