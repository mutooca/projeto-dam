package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlType(name = "ObterSaldoResponseDto")
public class ObterSaldoResponse {
    private String idUtilizador;
    private float saldo;
    private boolean encontrado;
}