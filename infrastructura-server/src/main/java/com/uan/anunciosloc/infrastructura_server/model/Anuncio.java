package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "anuncio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_anuncio")
    private UUID idAnuncio;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    private String categoria;

    @Column(nullable = false)
    private String estado; 

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(name = "id_local", nullable = false)
    private UUID idLocal;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @Column(name = "autor_email", nullable = false)
    private String autorEmail;

    @Column(name = "visivel_de")
    private LocalDateTime visivelDe;

    @Column(name = "visivel_ate")
    private LocalDateTime visivelAte;

    @Column(name = "tipo_politica")
    private String tipoPolitica;

    @Column(name = "politica_filtro")
    private String politicaFiltro;

}