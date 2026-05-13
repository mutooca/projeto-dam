package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;

     @Column(nullable = false)
    private String passwordHash;
    
    private Integer saldo;
    
    @Column(name = "role")
    private String role;  // "ADMIN" ou "USER"
    
    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;
    
    private boolean ativo;

    @Column(name = "kerberos_key")
    private String kerberosKey;
}