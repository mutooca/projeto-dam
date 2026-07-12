package com.anunciosloc.anunciosloc_server.uddi.dto;

import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlType(name = "LerSaldoResponseDto")
public class LerSaldoResponse {
    private boolean sucesso;
    private String email;
    private float saldo;
    private int versao;
    private String mensagem;
}