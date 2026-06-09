package com.uan.anunciosloc.infrastructura_server.soap.dto;



import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefinirRestricaoRequest")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DefinirRestricaoRequest {
    private String tipoRestricao;
    private String valorRestricao;
    private String descricao;
}