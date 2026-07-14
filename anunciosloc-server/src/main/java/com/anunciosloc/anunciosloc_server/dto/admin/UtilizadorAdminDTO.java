package com.anunciosloc.anunciosloc_server.dto.admin;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilizadorAdminDTO {
    private String email;
    private String nome;
    private Integer saldo;
    private LocalDateTime ultimoPost;
    private Long diasInativo;
    private String status; 
    private String ultimaLocalizacao; 
}
