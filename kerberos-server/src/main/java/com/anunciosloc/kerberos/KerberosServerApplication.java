package com.anunciosloc.kerberos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KerberosServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KerberosServerApplication.class, args);
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Servidor Kerberos iniciado!");
        System.out.println("  WSDL: http://localhost:8085/ws/kerberos?wsdl");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}