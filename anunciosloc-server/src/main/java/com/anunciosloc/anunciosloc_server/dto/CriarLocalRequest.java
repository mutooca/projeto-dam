package com.anunciosloc.anunciosloc_server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CriarLocalRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email do utilizador é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailUtilizador;

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

    private String ssidWifi;
}
