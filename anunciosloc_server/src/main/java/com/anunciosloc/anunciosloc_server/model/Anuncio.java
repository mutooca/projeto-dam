package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "anuncio")
@Data
@NoArgsConstructor
public class Anuncio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_anuncio", columnDefinition = "UUID")
    private UUID idAnuncio;
    
    private String titulo;
    
    @Column(length = 2000)
    private String conteudo;
    
    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;
    
    private String estado;
    
    private String categoria;
    
    
    @ManyToOne
    @JoinColumn(name = "id_local")
    private Local local;  
    
    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Utilizador autor;
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura; 
}