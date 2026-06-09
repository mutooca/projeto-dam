package com.anunciosloc.kerberos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "utilizadores")
@Data
@NoArgsConstructor
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_utilizador", columnDefinition = "UUID")
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String palavraChave;
    
    @Column(name = "kerberos_key")
    private String kerberosKey;  // derivada da password
    
    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;
    
    private boolean ativo;
} 