package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InfraestruturaRegistoRequest {

    @NotBlank(message = "Nome da infraestrutura é obrigatório")
    private String nome;

    @NotBlank(message = "URL da infraestrutura é obrigatória")
    @Pattern(regexp = "^http://.*", message = "URL deve começar com http://")
    private String url;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
    private Integer capacidade;

    @NotNull(message = "Bónus de entrega é obrigatório")
    @Min(value = 0, message = "Bónus de entrega não pode ser negativo")
    private Integer bonusEntrega;

    @NotNull(message = "Custo de post é obrigatório")
    @Min(value = 0, message = "Custo de post não pode ser negativo")
    private Integer custoPost;

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude inválida")
    @DecimalMax(value = "90.0", message = "Latitude inválida")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude inválida")
    @DecimalMax(value = "180.0", message = "Longitude inválida")
    private Double longitude;

    @NotNull(message = "Raio é obrigatório")
    @DecimalMin(value = "1.0", message = "Raio deve ser pelo menos 1 metro")
    private Double raio;

    private String restricoes;
}