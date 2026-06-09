package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "criarUtilizadorRequest", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"email", "password"})
public class CriarUtilizadorRequest {

    @XmlElement(name = "email", namespace = "http://kerberos.anunciosloc.com/")
    private String email;

    @XmlElement(name = "password", namespace = "http://kerberos.anunciosloc.com/")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}