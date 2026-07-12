package com.anunciosloc.anunciosloc_server.uddi.dto;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfraDisponivelUddiResponse {
    private String nome;
    private String url;
    private boolean registadoNaBd;
}