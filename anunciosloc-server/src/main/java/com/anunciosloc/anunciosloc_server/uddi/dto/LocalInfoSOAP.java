package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "LocalInfo", namespace = "http://infrastructura.anunciosloc.uan.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalInfo", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"idLocal", "nome", "latitude", "longitude", "raio", "ssid", "distancia"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalInfoSOAP {

    @XmlElement(name = "idLocal", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String idLocal;

    @XmlElement(name = "nome", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String nome;

    @XmlElement(name = "latitude", namespace = "http://infrastructura.anunciosloc.uan.com")
    private Double latitude;

    @XmlElement(name = "longitude", namespace = "http://infrastructura.anunciosloc.uan.com")
    private Double longitude;

    @XmlElement(name = "raio", namespace = "http://infrastructura.anunciosloc.uan.com")
    private Double raio;

    @XmlElement(name = "ssid", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String ssid;

    @XmlElement(name = "distancia", namespace = "http://infrastructura.anunciosloc.uan.com")
    private Double distancia;
}