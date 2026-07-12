package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "infraestrutura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Infraestrutura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_infraestrutura")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "url_endpoint", nullable = false)
    private String url;

    
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double raio;  

    @Column(nullable = false)
    private Integer capacidade;

    @Column(name = "bonus_entrega")
    private Integer bonusEntrega;

    @Column(name = "custo_post")
    private Integer custoPost;

    @Column(nullable = false)
    private boolean ativa;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;

  
    @Column(columnDefinition = "TEXT")
    private String restricoes;

    @Version
    @Column(name = "versao")
    private Integer versao;

    
    
}