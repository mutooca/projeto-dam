package com.anunciosloc.anunciosloc_server.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacaoSaldoDto {
    private String idUtilizador;
    private float saldo;
    private int versao;
}