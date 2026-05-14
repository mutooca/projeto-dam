package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://kerberos.anunciosloc.com/")
@XmlType(propOrder = {"valid", "message"})
public class ValidateTicketResponse {
    private boolean valid;
    private String message;
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}