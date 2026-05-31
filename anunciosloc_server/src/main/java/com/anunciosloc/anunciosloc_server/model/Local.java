package com.anunciosloc.anunciosloc_server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "local")
@Data
@NoArgsConstructor
public class Local {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     @Column(name = "id_local", columnDefinition = "UUID")
    private UUID idLocal;
    
    private String nome;
    
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
   
    @OneToOne
    @JoinColumn(name = "id_coordenada_wifi")
    private CoordenadaWifi coordenadaWifi;
   
    @OneToOne
    @JoinColumn(name = "id_coordenada_gps")
    private CoordenadaGps coordenadaGps;
    
    
    @OneToMany(mappedBy = "local")
    private List<Anuncio> anuncios = new ArrayList<>();
}