package com.anunciosloc.anunciosloc_server.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    // Gestor
    private String nomeGestor;
    private String emailGestor;

    // Utilizadores
    private int totalUtilizadores;
    private int utilizadoresAtivos;
    private int utilizadoresInativos;

    // Infraestruturas
    private int totalInfraestruturas;
    private int infraestruturasActivas;
    private int infraestruturasNoUddi;

    // Conexões
    private int totalConexoesActivas;

    // Anúncios
    private int totalAnuncios;
    private int totalAnunciosEntregues;
    private int totalAnunciosLidos;
    private int totalAnunciosAtivos;
    private int totalAnunciosRemovidos;

    // Lista de infraestruturas
    private List<InfraestruturaResponse> infraestruturas;
}