package com.anunciosloc.anunciosloc_server.dto;



import lombok.Data;



@Data
public class UtilizadorRequest {
    
    private String email;
    
    private String password;  
}
