package com.uan.anunciosloc.infrastructura_server.soap.dto;



import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CriarLocalResponse")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CriarLocalResponse {
    private String idLocal;
    private String nome;
    private String mensagem;
    private double latitude;
    private double longitude;
    private boolean sucesso;
    private double raio;;
}
