package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ActualizarUtilizadorRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(min = 6, message = "Password deve ter pelo menos 6 caracteres")
    private String novaPalavraChave;

    private String palavraChaveActual; // para confirmar identidade antes de mudar password

    private String preferenciaAnuncio;

}
