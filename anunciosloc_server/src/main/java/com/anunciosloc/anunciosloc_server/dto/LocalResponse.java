package com.anunciosloc.anunciosloc_server.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalResponse {
    private String idLocal;
    private String nome;
    private double latitude;
    private double longitude;
    private double raio;
}