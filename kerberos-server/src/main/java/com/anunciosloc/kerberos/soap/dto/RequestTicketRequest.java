package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://kerberos.anunciosloc.com/")
@XmlType(propOrder = {"email", "clientNonce"})
public class RequestTicketRequest {
    private String email;
    private String clientNonce;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClientNonce() { return clientNonce; }
    public void setClientNonce(String clientNonce) { this.clientNonce = clientNonce; }
}