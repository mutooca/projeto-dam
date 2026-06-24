package com.anunciosloc.anunciosloc_server.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistarInfraRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
    private Integer capacidade;
    @NotNull(message = "Bonus de entrega é obrigatório")
    @Min(value = 0, message = "Bonus de entrega não pode ser negativo")
    private Integer bonusEntrega;
    @NotNull(message = "Custo de post é obrigatório")
    @Min(value = 0, message = "Custo de post não pode ser negativo")
    private Integer custoPost;

    @NotBlank(message = "Email do gestor é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailGestor;

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