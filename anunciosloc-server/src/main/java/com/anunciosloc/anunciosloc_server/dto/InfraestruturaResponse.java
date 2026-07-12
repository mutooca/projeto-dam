package com.anunciosloc.anunciosloc_server.dto;



import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfraestruturaResponse {
    private UUID id;
    private String nome;
    private Double latitude;
    private Double longitude;
    private Integer bonusEntrega;
    private Integer capacidade;
    private Integer custoPost;
    private Integer conexoesDisponiveis;
    private Integer conexoesAtuais;
    private Double distanciaKm;
    private Double raio;
    private Integer totalAnuncios;
    private Integer totalEntregas;
    private List<CoordenadaResponse> coordenadas;

}
