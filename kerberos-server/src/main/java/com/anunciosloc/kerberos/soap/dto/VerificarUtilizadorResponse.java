package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "verificarUtilizadorResponse", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"existe", "message"})
public class VerificarUtilizadorResponse {

    @XmlElement(name = "existe", namespace = "http://kerberos.anunciosloc.com/")
    private boolean existe;

    @XmlElement(name = "message", namespace = "http://kerberos.anunciosloc.com/")
    private String message;

    public boolean isExiste() {
        return existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}