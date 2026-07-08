package com.uan.anunciosloc.infrastructura_server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalInfo {
    private String id;
    private String nome;

    // "GPS" ou "WIFI"
    private String tipoCoordenada;

    
    private CoordenadaGps coordenadaGps;

    
    private List<CoordenadaWifi> coordenadasWifi;
}