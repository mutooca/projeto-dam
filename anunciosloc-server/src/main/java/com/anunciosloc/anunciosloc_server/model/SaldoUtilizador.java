package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saldo_utilizador")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaldoUtilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "id_utilizador", nullable = false, unique = true)
    private Utilizador utilizador;

    @Column(nullable = false)
    private Integer saldo;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
}
