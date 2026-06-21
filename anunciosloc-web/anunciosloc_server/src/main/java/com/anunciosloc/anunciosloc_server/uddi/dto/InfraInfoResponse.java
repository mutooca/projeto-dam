package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InfraInfoResponse {
    private String nome;
    private String urlEndpoint;
    private int capacidade;
    private int bonusEntrega;
    private int custoPost;
    private int totalAnuncios;
    private int totalEntregas;
    private int totalConexoes;
    private int conectadosAgora;
    private int conexoesDisponiveis;
}