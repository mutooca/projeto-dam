package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Data;

@Data
public class InfraInfoResponse {
    private String id;
    private String nome;
    private String url;
    private Integer capacidade;
    private Integer bonusEntrega;
    private Integer custoPost;
    private boolean ativa;
    private Integer totalLocais;
    private Integer totalAnuncios;
    private Integer totalEntregas;
    private Integer totalConexoes;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String mensagem;
    private boolean sucesso;
}