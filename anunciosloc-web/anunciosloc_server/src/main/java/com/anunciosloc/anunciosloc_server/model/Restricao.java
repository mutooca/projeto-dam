package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restricao")
@Data
@NoArgsConstructor
public class Restricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restricao")
    private Long idRestricao;
    
    @Column(name = "tipo_restricao")
    private String tipoRestricao;  
    
    @Column(name = "valor_restricao")
    private String valorRestricao;  
    
    private String descricao;
    
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
}