package com.anunciosloc.anunciosloc_server.dto.admin;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardEstatisticasDTO {
    private Long totalUtilizadores;
    private Long utilizadoresAtivos;
    private Long utilizadoresInativos;
    private Long totalInfraestruturas;
    private Long infraestruturasOnline;
    private Long infraestruturasOffline;
    private Long totalLocais;
    private Long totalAnuncios;
    private Long totalEntregas;
    private Long totalConexoes;
    private LocalDateTime ultimaAtualizacao;
}
