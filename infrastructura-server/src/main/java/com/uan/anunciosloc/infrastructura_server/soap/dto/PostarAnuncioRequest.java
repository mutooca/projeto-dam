package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostarAnuncioRequest")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostarAnuncioRequest {

    private String emailAutor;
    private String idLocal;          
    private String titulo;
    private String conteudo;
    private String tipoPolitica;
    private String politicaFiltro;
    private String visivelDe;
    private String visivelAte;
}