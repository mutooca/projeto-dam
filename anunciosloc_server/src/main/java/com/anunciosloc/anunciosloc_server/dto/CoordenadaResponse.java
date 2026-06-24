package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoordenadaResponse {
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String ssidWifi;
}
