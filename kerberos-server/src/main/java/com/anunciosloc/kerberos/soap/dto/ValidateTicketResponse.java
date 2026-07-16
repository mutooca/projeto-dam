package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "validateTicketResponse", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"valid", "message", "email"})
public class ValidateTicketResponse {

    @XmlElement(name = "valid", namespace = "http://kerberos.anunciosloc.com/")
    private boolean valid;

    @XmlElement(name = "message", namespace = "http://kerberos.anunciosloc.com/")
    private String message;

    // Campo aditivo (opcional): email do utilizador autenticado pelo ticket+authenticator válidos.
    // Mantido depois de "message" e sem alterar os elementos existentes para não quebrar clientes atuais.
    @XmlElement(name = "email", namespace = "http://kerberos.anunciosloc.com/")
    private String email;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}