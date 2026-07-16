package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditarLocalResponseSOAP {
    private String idLocal;
    private String nome;
    private String mensagem;
    private double latitude;
    private double longitude;
    private boolean sucesso;
    private double raio;
    private String ssidWifi;
}
