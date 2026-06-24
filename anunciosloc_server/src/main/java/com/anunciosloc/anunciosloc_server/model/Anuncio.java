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

    // Tipo de política: "WHITELIST", "BLACKLIST" ou null (sem restrição)
    @Column(name = "tipo_politica")
    private String tipoPolitica;

    // Filtro: pares chave=valor separados por vírgula
    // "profissao=Estudante,bairro=Maianga"
    @Column(name = "politica_filtro")
    private String politicaFiltro;

    // Janela de tempo de visibilidade
    @Column(name = "visivel_de")
    private LocalDateTime visivelDe;

    @Column(name = "visivel_ate")
    private LocalDateTime visivelAte;
}