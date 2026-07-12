package com.anunciosloc.anunciosloc_server.dto;

import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfraestruturaAtivaDTO {
    private String nome;
    private String url;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private Double distancia;
    private InfraProxy proxy;
    private boolean ativa;
}