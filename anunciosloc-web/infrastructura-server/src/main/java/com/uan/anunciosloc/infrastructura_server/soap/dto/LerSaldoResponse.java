package com.uan.anunciosloc.infrastructura_server.soap.dto;



import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LerSaldoResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LerSaldoResponse {
    private String idUtilizador;
    private float saldo;
    private int versao;
    private boolean encontrado;
}