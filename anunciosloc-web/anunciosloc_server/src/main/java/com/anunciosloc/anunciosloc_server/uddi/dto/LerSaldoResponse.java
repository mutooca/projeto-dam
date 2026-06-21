package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LerSaldoResponse {
    private String idUtilizador;
    private float saldo;
    private int versao;
    private boolean encontrado;
}