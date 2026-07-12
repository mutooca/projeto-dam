package com.uan.anunciosloc.infrastructura_server.soap.dto;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DiasInatividadeResponse", 
         namespace = "http://infrastructura.anunciosloc.uan.com",
         propOrder = {"sucesso", "email", "diasInativo", "inativo", "ultimoPost", "mensagem"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiasInatividadeResponse {

    @XmlElement(name = "sucesso", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean sucesso;

    @XmlElement(name = "email", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String email;

    @XmlElement(name = "diasInativo", namespace = "http://infrastructura.anunciosloc.uan.com")
    private int diasInativo;

    @XmlElement(name = "inativo", namespace = "http://infrastructura.anunciosloc.uan.com")
    private boolean inativo;

    @XmlElement(name = "ultimoPost", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String ultimoPost;

    @XmlElement(name = "mensagem", namespace = "http://infrastructura.anunciosloc.uan.com")
    private String mensagem;
}