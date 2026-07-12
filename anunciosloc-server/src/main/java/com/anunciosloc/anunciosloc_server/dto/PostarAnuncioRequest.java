package com.anunciosloc.anunciosloc_server.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostarAnuncioRequest {

    @NotBlank(message = "Email do autor é obrigatório")
    @Email(message = "Formato de email inválido")
    private String emailAutor;

    @NotBlank(message = "Nome do local é obrigatório")
    private String nomeLocal;  

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    private String titulo;

    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 10, max = 2000, message = "Conteúdo deve ter entre 10 e 2000 caracteres")
    private String conteudo;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    private String tipoPolitica;

    private String politicaFiltro;

    private LocalDateTime visivelDe;
    private LocalDateTime visivelAte;
}