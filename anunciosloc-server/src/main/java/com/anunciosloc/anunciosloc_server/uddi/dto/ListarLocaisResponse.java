package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "ListarLocaisResponse", namespace = "http://infrastructura.anunciosloc.uan.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListarLocaisResponse", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"sucesso", "locais", "mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListarLocaisResponse {

    @XmlElement(name = "sucesso", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean sucesso;

    @XmlElement(name = "locais", namespace = "http://infrastructura.anunciosloc.uan.com")
    private List<LocalInfoSOAP> locais;

    @XmlElement(name = "mensagem", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;
}