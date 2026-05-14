package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://kerberos.anunciosloc.com/")
@XmlType(propOrder = {"sessionId"})
public class VerifySessionRequest {
    private String sessionId;
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}