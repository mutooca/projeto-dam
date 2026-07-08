package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceberAnunciosResponse", propOrder = {"sucesso", "anuncios", "mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceberAnunciosResponse {

    @XmlElement(name = "sucesso")
    private boolean sucesso;

    @XmlElement(name = "anuncios")
    private List<AnuncioInfo> anuncios;

    @XmlElement(name = "mensagem")
    private String mensagem;
}