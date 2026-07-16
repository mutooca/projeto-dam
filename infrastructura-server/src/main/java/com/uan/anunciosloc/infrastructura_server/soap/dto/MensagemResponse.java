package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MensagemResponse",
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"sucesso", "mensagem", "estado", "idAnuncio"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemResponse {

    @XmlElement(name = "sucesso", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean sucesso;

    @XmlElement(name = "mensagem", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;


    @XmlElement(name = "estado", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String estado;

    @XmlElement(name = "idAnuncio", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String idAnuncio;
}