package com.uan.anunciosloc.infrastructura_server.soap.dto;



import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObterSaldoResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ObterSaldoResponse {
    private String idUtilizador;
    private float saldo;
    private boolean encontrado;
}