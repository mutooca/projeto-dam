package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecolocarInfraRequest {

    @NotBlank(message = "Email do gestor é obrigatório")
    @Email
    private String emailGestor;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double novaLatitude;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double novaLongitude;

    @NotNull
    @DecimalMin(value = "1.0")
    private Double novoRaio;
}