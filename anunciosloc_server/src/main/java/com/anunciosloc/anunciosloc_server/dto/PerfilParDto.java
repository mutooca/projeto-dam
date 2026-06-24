package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilParDto {

    @NotBlank(message = "Chave é obrigatória")
    private String chave;

    @NotBlank(message = "Valor é obrigatório")
    private String valor;
}