package com.uan.anunciosloc.infrastructura_server.model;

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
    @Column(name = "id_saldo")
    private UUID idSaldo;

    @Column(name = "email_utilizador", nullable = false)
    private String emailUtilizador;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @Column(name = "saldo_parcial")
    @Builder.Default
    private Integer saldoParcial = 0;

    @Column(name = "versao")
    @Builder.Default
    private Integer versao = 0;

    @Column(name = "pontos_ganhos")
    @Builder.Default
    private Integer pontosGanhos = 0;

    @Column(name = "pontos_gastos")
    @Builder.Default
    private Integer pontosGastos = 0;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
}