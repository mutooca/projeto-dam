package com.anunciosloc.anunciosloc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditarLocalResponse {
    private String idLocal;
    private String nome;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String ssidWifi;
    private boolean sucesso;
    private String mensagem;
}
