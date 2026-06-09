package com.anunciosloc.anunciosloc_server.dto;

import lombok.Data;

@Data
public class CriarLocalRequest {
    private String nome;
    private Double latitude;
    private Double longitude;
    private Integer raio;
    private String ssidWifi;
    private Double latUtilizador; 
    private Double lonUtilizador;
}