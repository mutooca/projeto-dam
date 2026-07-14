package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilItem {
    @NotBlank(message = "Chave é obrigatória")
    private String chave;

    @NotBlank(message = "Valor é obrigatório")
    private String valor;
}
