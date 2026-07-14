package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRequest {

    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotNull(message = "Perfil não pode ser vazio")
    private List<PerfilItem> perfil;

}