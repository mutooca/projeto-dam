package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://kerberos.anunciosloc.com/")
@XmlType(propOrder = {"success", "ticket", "sessionKey", "sessionId", "message"})
public class RequestTicketResponse {
    private boolean success;
    private String ticket;
    private String sessionKey;
    private String sessionId;
    private String message;
    
    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getTicket() { return ticket; }
    public void setTicket(String ticket) { this.ticket = ticket; }
    public String getSessionKey() { return sessionKey; }
    public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}