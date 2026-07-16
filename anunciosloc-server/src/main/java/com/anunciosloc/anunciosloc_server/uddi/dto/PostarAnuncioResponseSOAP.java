package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostarAnuncioResponseSOAP {
    private boolean sucesso;
    private String idAnuncio;
    private String mensagem;
}
