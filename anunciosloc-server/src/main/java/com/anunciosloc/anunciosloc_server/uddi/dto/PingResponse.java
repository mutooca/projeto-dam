package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PingResponse",  
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PingResponse {

    @XmlElement(name = "return", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;
}