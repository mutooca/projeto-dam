package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestricaoInfo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestricaoInfo {
    private String idRestricao;
    private String tipoRestricao;
    private String valorRestricao;
    private String descricao;
}