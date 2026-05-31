package com.anunciosloc.anunciosloc_server.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coordenada_wifi")
@Data
@NoArgsConstructor
public class CoordenadaWifi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_coordenada_wifi",columnDefinition = "UUID")
    private UUID idCoordenadaWifi;
    
    private String ssid;  // Nome da rede WiFi
}