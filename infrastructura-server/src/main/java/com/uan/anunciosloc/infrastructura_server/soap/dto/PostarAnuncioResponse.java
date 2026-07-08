package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostarAnuncioResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostarAnuncioResponse {
    private boolean sucesso;
    private String idAnuncio;
    private String mensagem;
}