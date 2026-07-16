package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoLeituraSOAP {
    private boolean sucesso;
    private String mensagem;
    private String estado;
    private String idAnuncio;
    private String autorEmail;
    private boolean leituraNova;
}
