package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstatisticasSistemaResponse {
    private Long totalUtilizadores;
    private Long totalInfraestruturas;
    private Long totalConexoesAtivas;
    private Long totalAnuncios;
    private Long totalEntregas;
    private Long totalLocais;
}