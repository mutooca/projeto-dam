package com.uan.anunciosloc.infrastructura_server.soap.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EscreverSaldoResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EscreverSaldoResponse {
    private boolean sucesso;
    private int versaoActual;
    private String mensagem;
}