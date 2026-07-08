package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfraInfoResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObterInfraResponse {
    private boolean sucesso;
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
}