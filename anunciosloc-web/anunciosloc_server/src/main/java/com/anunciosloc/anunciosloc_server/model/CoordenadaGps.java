package com.anunciosloc.anunciosloc_server.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coordenada_gps")
@Data
@NoArgsConstructor
public class CoordenadaGps {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_coordenada_gps",columnDefinition = "UUID")
    private UUID idCoordenadaGps;
    
    private Double latitude;
    private Double longitude;
    private Integer raio;
}