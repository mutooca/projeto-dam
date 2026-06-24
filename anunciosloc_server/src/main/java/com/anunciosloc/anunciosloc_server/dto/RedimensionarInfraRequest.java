package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedimensionarInfraRequest {

    @NotBlank(message = "Email do gestor é obrigatório")
    @Email
    private String emailGestor;

    @NotNull
    @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
    private Integer novaCapacidade;

    @NotNull
    @Min(value = 0)
    private Integer novoBonusEntrega;

    @NotNull
    @Min(value = 0)
    private Integer novoCustoPost;
}