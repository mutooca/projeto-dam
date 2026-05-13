package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;  
    
    @Column(length = 2000)
    private String conteudo;
    
    @ManyToOne
    private Utilizador autor;
    
    @ManyToOne
    private Infraestrutura local;
    
    private LocalDateTime dataPost;
    
    private boolean entregue;

    @ManyToMany
    @JoinTable(
        name = "mensagem_visualizada",
        joinColumns = @JoinColumn(name = "mensagem_id"),
        inverseJoinColumns = @JoinColumn(name = "utilizador_id")
    )
    private List<Utilizador> visualizadaPor = new ArrayList<>();
    
    // Número total de visualizações (para estatística)
    private int totalVisualizacoes;
}