package com.anunciosloc.anunciosloc_server.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilResponse {
    private String email;
    private String nome;
    private List<PerfilParDto> pares;      // pares privados do utilizador
    private List<String> chavesPublicas;   // todas as chaves do sistema
}