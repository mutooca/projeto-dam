package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceberAnunciosRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReceberAnunciosRequest {
    private String email;
    private String idLocal;
}