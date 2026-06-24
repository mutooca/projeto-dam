package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfraDisponivelUddiResponse {
    private String nome;
    private String url;
    private boolean registadoNaBd;
}