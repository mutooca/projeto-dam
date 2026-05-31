package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoordenadaResponse {
    private Double latitude;
    private Double longitude;
    private Integer raio;
    private String ssidWifi;
}
