package com.anunciosloc.anunciosloc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarLidoRequest {
    private String idAnuncio;
    private String emailUtilizador;
}
