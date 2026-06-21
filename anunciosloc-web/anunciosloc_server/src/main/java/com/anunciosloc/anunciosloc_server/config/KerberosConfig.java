package com.anunciosloc.anunciosloc_server.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KerberosConfig {
    
    @Value("${kerberos.soap.url}")
    private String soapUrl;
    
    public String getSoapUrl() {
        return soapUrl;
    }
}