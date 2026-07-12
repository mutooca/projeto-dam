package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MensagemResponse", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"sucesso", "mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemResponse {

    @XmlElement(name = "sucesso", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean sucesso;

    @XmlElement(name = "mensagem", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;
}