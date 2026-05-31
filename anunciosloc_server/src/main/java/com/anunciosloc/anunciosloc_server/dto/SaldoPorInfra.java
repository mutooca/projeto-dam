package com.anunciosloc.anunciosloc_server.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaldoPorInfra {
    private UUID idInfra;
    private String nomeInfra;
    private Integer saldoParcial;
    private Integer pontosGanhos;
    private Integer pontosGastos;
}
