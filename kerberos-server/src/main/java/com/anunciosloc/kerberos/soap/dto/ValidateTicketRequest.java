package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://kerberos.anunciosloc.com/")
@XmlType(propOrder = {"ticket", "authenticator"})
public class ValidateTicketRequest {
    private String ticket;
    private String authenticator;
    
    public String getTicket() { return ticket; }
    public void setTicket(String ticket) { this.ticket = ticket; }
    public String getAuthenticator() { return authenticator; }
    public void setAuthenticator(String authenticator) { this.authenticator = authenticator; }
}