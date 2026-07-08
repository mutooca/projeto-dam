package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "coordenada_gps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordenadaGps {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_coordenada_gps")
    private UUID id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double raio;
}