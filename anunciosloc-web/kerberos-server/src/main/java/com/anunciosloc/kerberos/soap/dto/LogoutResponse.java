package com.anunciosloc.kerberos.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "logoutResponse", namespace = "http://kerberos.anunciosloc.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"success", "message"})
public class LogoutResponse {

    @XmlElement(name = "success", namespace = "http://kerberos.anunciosloc.com/")
    private boolean success;

    @XmlElement(name = "message", namespace = "http://kerberos.anunciosloc.com/")
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}