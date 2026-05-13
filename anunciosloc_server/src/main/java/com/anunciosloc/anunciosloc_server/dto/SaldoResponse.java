package com.anunciosloc.anunciosloc_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaldoResponse {
    private String email;
    private Integer saldo;
}
