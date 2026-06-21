package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "requestTicketRequest", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"email", "clientNonce"})
public class RequestTicketRequest {

    @XmlElement(name = "email", namespace = "http://kerberos.anunciosloc.com/")
    private String email;

    @XmlElement(name = "clientNonce", namespace = "http://kerberos.anunciosloc.com/")
    private String clientNonce;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClientNonce() { return clientNonce; }
    public void setClientNonce(String clientNonce) { this.clientNonce = clientNonce; }
}