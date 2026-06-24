package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlType(name = "EscreverSaldoResponseDto")
public class EscreverSaldoResponse {
    private boolean sucesso;
    private int versaoActual;
    private String mensagem;
}