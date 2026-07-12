package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor
public class PostarAnuncioRequestSOAP {
    private String emailAutor;
    private String idLocal;
    private String titulo;
    private String conteudo;
    private String categoria;
    private String tipoPolitica;
    private String politicaFiltro;
    private String visivelDe;
    private String visivelAte;
}