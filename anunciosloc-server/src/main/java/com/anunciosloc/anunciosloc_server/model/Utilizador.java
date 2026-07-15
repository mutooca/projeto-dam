package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    @Column(name = "id_utilizador", columnDefinition = "UUID")
    private UUID idUtilizador  ;

    @Column(unique = true, nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "palavra_chave", nullable = false)
    private String palavraChave;
    
    private Integer saldo;
    
    @Column(name = "role")
    private String role;  // "ADMIN" ou "USER"
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultimo_post")
    private LocalDateTime dataUltimoPost;

    @Column(name = "preferencia_anuncio")
    private String preferenciaAnuncio;
    private boolean ativo;

    @Column(name = "kerberos_key")
    private String kerberosKey;

    
    @Column(name = "ultima_localizacao")
    private String ultimaLocalizacao;  

    @Column(name = "ultima_atualizacao_localizacao")
    private LocalDateTime ultimaAtualizacaoLocalizacao;
}