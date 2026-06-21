package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CriarLocalRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CriarLocalRequest {
    private String nome;
    // "GPS" ou "WIFI"
    private String tipoCoordenada;

    // GPS
    private Double latitude;
    private Double longitude;
    private Double raioMetros;

    
    private String ssids;
}