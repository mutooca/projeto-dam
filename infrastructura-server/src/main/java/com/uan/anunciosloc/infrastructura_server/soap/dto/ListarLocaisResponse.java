package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListarLocaisResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ListarLocaisResponse {
    private List<LocalInfo> locais;
    private boolean sucesso;
    private String mensagem;
}