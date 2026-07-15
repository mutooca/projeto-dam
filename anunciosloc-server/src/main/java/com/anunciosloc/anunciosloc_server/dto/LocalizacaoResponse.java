package com.anunciosloc.anunciosloc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoResponse {
    private boolean sucesso;
    private String mensagem;
    private String email;
    private Double latitude;
    private Double longitude;
    private Integer locaisProximos;
}