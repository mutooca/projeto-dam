package com.anunciosloc.anunciosloc_server.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfraestruturaResponse {
    private Long id;
    private String nome;
    private Double latitude;
    private Double longitude;
    private Integer capacidade;
    private Integer conexoesDisponiveis;
    private Double distanciaKm;
    private Integer totalAnuncios;
    private Integer totalEntregas;
}
