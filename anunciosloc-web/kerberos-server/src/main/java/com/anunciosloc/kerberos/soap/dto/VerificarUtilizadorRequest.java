package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "verificarUtilizadorRequest", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"email"})
public class VerificarUtilizadorRequest {

    @XmlElement(name = "email", namespace = "http://kerberos.anunciosloc.com/")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}