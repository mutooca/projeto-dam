package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObterSaldoResponse {
    private String idUtilizador;
    private float saldo;
    private boolean encontrado;
}