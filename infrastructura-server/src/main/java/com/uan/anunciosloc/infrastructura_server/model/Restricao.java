package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "restricao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restricao {

    @Id
    @GeneratedValue(strategy   = GenerationType.UUID)
    @Column(name = "id_restricao")
    private UUID idRestricao;

    @Column(name = "tipo_restricao")
    private String tipoRestricao;

    @Column(name = "valor_restricao")
    private String valorRestricao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

}