package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfraInfoResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
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