package com.anunciosloc.kerberos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "kerberos_sessoes")
@Data
@NoArgsConstructor
public class KerberosSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String sessionId;
    
    @Column(nullable = false)
    private String emailUtilizador;
    
    @Column(length = 500)
    private String sessionKey;
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private LocalDateTime expiraEm;
    
    @Column(nullable = false)
    private boolean ativo;
}