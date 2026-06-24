package com.anunciosloc.anunciosloc_server.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaldoResponse {
    private String email;
    private String nome;
    private Integer saldoGlobal;
    private String preferencias;    // Para F6 (Atributos)
    private Long totalAnuncios;     // Para estatística no perfil
    private Long totalEntregas;     // Para estatística no perfil
    private List<SaldoPorInfra> saldosPorInfra;
}
