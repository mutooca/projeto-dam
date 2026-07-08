package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "local")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_local")
    private UUID idLocal;

    @Column(nullable = false)
    private String nome;

    @Column(name = "criado_por")
    private String criadoPor;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_coordenada_gps")
    private CoordenadaGps coordenadaGps; 

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_coordenada_wifi")
    private CoordenadaWifi coordenadaWifi;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

}