package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EscreverSaldoResponse {
    private boolean sucesso;
    private int versaoActual;
    private String mensagem;
}