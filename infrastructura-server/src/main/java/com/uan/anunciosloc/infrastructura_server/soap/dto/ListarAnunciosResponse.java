package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListarAnunciosResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ListarAnunciosResponse {
    private List<AnuncioInfo> anuncios;
    private boolean sucesso;
    private String mensagem;
}