package com.anunciosloc.anunciosloc_server.client;
public class VerifyResponse {
    private boolean valid;
    private String message;
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
}