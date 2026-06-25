package com.anunciosloc.anunciosloc_server.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Data
@Builder
public class SaldoResponse {
    private String email;
    private String nome;
    private Integer saldoGlobal;
    private List<SaldoPorInfra> saldosPorInfra;
}
