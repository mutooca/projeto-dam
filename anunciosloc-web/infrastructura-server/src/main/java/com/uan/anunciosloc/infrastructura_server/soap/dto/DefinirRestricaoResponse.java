package com.uan.anunciosloc.infrastructura_server.soap.dto;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefinirRestricaoResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DefinirRestricaoResponse {
    private String idRestricao;
    private boolean sucesso;
    private String mensagem;
}
