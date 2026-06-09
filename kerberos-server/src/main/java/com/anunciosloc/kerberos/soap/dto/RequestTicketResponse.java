package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "requestTicketResponse", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"success", "ticket", "sessionKey", "sessionId", "message"})
public class RequestTicketResponse {

    @XmlElement(name = "success", namespace = "http://kerberos.anunciosloc.com/")
    private boolean success;

    @XmlElement(name = "ticket", namespace = "http://kerberos.anunciosloc.com/")
    private String ticket;

    @XmlElement(name = "sessionKey", namespace = "http://kerberos.anunciosloc.com/")
    private String sessionKey;

    @XmlElement(name = "sessionId", namespace = "http://kerberos.anunciosloc.com/")
    private String sessionId;

    @XmlElement(name = "message", namespace = "http://kerberos.anunciosloc.com/")
    private String message;

    
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