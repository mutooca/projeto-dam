package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedimensionarInfraRequest {

    @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
    private Integer capacidade;

    @Min(value = 0, message = "Bónus de entrega não pode ser negativo")
    private Integer bonusEntrega;

    @Min(value = 0, message = "Custo de post não pode ser negativo")
    private Integer custoPost;

    @Min(value = 1, message = "Raio deve ser pelo menos 1 metro")
    private Double raio;
}