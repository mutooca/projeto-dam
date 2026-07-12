package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecolocarInfraRequest {

    @NotNull(message = "Latitude é obrigatória")
    @Min(value = -90, message = "Latitude deve estar entre -90 e 90")
    @Max(value = 90, message = "Latitude deve estar entre -90 e 90")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @Min(value = -180, message = "Longitude deve estar entre -180 e 180")
    @Max(value = 180, message = "Longitude deve estar entre -180 e 180")
    private Double longitude;

    @NotNull(message = "Raio é obrigatório")
    @Min(value = 1, message = "Raio deve ser pelo menos 1 metro")
    private Double raio;
}