package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarLocalRequestSOAP {
    private String nome;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String emailUtilizador;
    private String ssidWifi;
}