package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceberAnunciosRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReceberAnunciosRequest {
    private String email;
    private String idLocal;
    private List<PerfilItem> perfil;
}