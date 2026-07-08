package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "coordenada_wifi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordenadaWifi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_coordenada_wifi")
    private UUID id;

    @Column(nullable = false)
    private String ssid;
}