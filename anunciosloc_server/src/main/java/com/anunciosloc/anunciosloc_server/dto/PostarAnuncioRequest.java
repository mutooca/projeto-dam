package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostarAnuncioRequest {

    @NotBlank(message = "Email do autor é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailAutor;

    @NotNull(message = "Local é obrigatório")
    private UUID localId;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    private String titulo;

    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 10, max = 2000, message = "Conteúdo deve ter entre 10 e 2000 caracteres")
    private String conteudo;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    private String tipoPolitica;

    // "profissao=Estudante,bairro=Maianga"
    private String politicaFiltro;

    // Janela de tempo (opcional)
    private LocalDateTime visivelDe;
    private LocalDateTime visivelAte;
}