package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UddiInstanciaResponse {
    private String serviceName;
    private String serviceUrl;
    private String porta;
    private String status;  
    private boolean jaRegistada;  
}