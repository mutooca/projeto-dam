package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entrega_anuncio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaAnuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_entrega")
    private UUID idEntrega;

    @Column(name = "id_anuncio", nullable = false)
    private UUID idAnuncio;

    @Column(name = "email_utilizador", nullable = false)
    private String emailUtilizador;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @Column(name = "estado_entrega")
    private String estadoEntrega; 

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(name = "modo")
    private String modo; 

    @Column(name = "num_entrega")
    private Integer numEntrega;

}