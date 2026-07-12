package com.anunciosloc.anunciosloc_server.client;


import lombok.Data;

@Data
public class TicketResponseAdmin {
    


    private boolean success;
    private String ticket;
    private String sessionKey;
    private String sessionId;
    private String message;
    private String role;  
    
}

