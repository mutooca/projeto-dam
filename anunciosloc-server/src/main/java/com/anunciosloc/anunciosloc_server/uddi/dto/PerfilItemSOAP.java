package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerfilItem", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"chave", "valor"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilItemSOAP {

    @XmlElement(name = "chave", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String chave;

    @XmlElement(name = "valor", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String valor;
}