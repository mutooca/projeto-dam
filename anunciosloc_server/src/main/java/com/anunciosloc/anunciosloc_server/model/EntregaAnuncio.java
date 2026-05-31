package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entrega_anuncio")
@Data
@NoArgsConstructor
public class EntregaAnuncio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_entrega_anuncio",columnDefinition = "UUID")
    private UUID idEntregaAnuncio;
    
    @Column(name = "num_entrega")
    private Integer numEntrega;  
    
    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;
    
    @Column(name = "estado_entrega")
    private String estadoEntrega;  // "PENDENTE", "ENTREGUE", "VISUALIZADO"
    
    
    @ManyToOne
    @JoinColumn(name = "id_anuncio")
    private Anuncio anuncio;
    
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
    
   
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_utilizador")
    private Utilizador utilizador;
}