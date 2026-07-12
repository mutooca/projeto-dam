package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnuncioInfo", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {
             "id", "titulo", "conteudo", "categoria", 
             "autorEmail", "dataPublicacao", "visivelDe", 
             "visivelAte", "tipoPolitica", "politicaFiltro"
         })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioInfoSOAP {

    @XmlElement(name = "id", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String id;

    @XmlElement(name = "titulo", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String titulo;

    @XmlElement(name = "conteudo", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String conteudo;

    @XmlElement(name = "categoria", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String categoria;

    @XmlElement(name = "autorEmail", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String autorEmail;

    @XmlElement(name = "dataPublicacao", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String dataPublicacao;

    @XmlElement(name = "visivelDe", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String visivelDe;

    @XmlElement(name = "visivelAte", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String visivelAte;

    @XmlElement(name = "tipoPolitica", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String tipoPolitica;

    @XmlElement(name = "politicaFiltro", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String politicaFiltro;
}