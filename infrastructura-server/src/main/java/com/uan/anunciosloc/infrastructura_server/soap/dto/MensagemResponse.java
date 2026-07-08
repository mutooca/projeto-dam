package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MensagemResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MensagemResponse {
    private boolean sucesso;
    private String mensagem;
}