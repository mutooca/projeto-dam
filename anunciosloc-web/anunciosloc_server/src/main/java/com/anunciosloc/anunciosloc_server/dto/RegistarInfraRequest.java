package com.anunciosloc.anunciosloc_server.dto;

import lombok.Data;

@Data
public class RegistarInfraRequest {
    private String nome;
    private Integer capacidade;
    private Integer bonusEntrega;
    private Integer custoPost;
    private String emailGestor;  // email do ADMIN que criou
    private Double latitude;     // coordenada GPS do local principal
    private Double longitude;
    private Integer raio;
     private String ssidWifi;
    
}