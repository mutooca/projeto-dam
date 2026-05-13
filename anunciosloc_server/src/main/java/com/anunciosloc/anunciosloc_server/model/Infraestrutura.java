package com.anunciosloc.anunciosloc_server.model;



import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity

public class Infraestrutura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;  
    
    private Double latitude;
    private Double longitude;
    
    private Integer capacidade;  
    
    private Integer conexoesAtuais;  
    
    private Integer totalAnuncios;    
    private Integer totalEntregas;    
}
