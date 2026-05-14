package com.anunciosloc.kerberos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilizadores")
@Data
@NoArgsConstructor
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(name = "kerberos_key")
    private String kerberosKey;  // Chave derivada da password
    
    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;
    
    private boolean ativo;
}