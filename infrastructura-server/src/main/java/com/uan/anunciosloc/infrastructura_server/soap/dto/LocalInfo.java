package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalInfo")
@Data 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class LocalInfo {
    private String idLocal;
    private String nome;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String ssid;
}