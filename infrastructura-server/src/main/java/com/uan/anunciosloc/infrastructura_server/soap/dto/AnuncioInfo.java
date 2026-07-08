package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnuncioInfo", propOrder = {
    "id", "titulo", "conteudo", "categoria", 
    "autorEmail", "dataPublicacao", "visivelDe", 
    "visivelAte", "tipoPolitica", "politicaFiltro"
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioInfo {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "titulo")
    private String titulo;

    @XmlElement(name = "conteudo")
    private String conteudo;

    @XmlElement(name = "categoria")
    private String categoria;

    @XmlElement(name = "autorEmail")
    private String autorEmail;

    @XmlElement(name = "dataPublicacao")
    private String dataPublicacao;

    @XmlElement(name = "visivelDe")
    private String visivelDe;

    @XmlElement(name = "visivelAte")
    private String visivelAte;

    @XmlElement(name = "tipoPolitica")
    private String tipoPolitica;

    @XmlElement(name = "politicaFiltro")
    private String politicaFiltro;
}