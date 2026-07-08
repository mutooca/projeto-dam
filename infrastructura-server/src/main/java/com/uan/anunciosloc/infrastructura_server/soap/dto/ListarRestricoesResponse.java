package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListarRestricoesResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ListarRestricoesResponse {
    private List<RestricaoInfo> restricoes;
}