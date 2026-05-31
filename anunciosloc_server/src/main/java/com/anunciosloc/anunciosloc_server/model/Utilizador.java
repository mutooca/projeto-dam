package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "autor")
    private List<Anuncio> anuncios = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilizador")
    private List<Conexao> conexoes = new ArrayList<>();

    @OneToMany(mappedBy = "utilizador")
    private List<EntregaAnuncio> entregas = new ArrayList<>();
    
    
    private boolean ativo;

    @Column(name = "kerberos_key")
    private String kerberosKey;
}