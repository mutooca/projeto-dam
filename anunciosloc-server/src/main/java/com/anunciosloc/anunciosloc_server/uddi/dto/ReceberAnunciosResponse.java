package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceberAnunciosResponse", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"sucesso", "anuncios", "mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceberAnunciosResponse {

    @XmlElement(name = "sucesso", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean sucesso;

    @XmlElement(name = "anuncios", namespace = "http://infrastructura.anunciosloc.uan.com")
    private List<AnuncioInfoSOAP> anuncios;

    @XmlElement(name = "mensagem", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;
}