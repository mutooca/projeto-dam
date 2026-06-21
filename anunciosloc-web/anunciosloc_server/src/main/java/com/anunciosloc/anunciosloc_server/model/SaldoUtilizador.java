package com.anunciosloc.anunciosloc_server.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saldo_utilizador")
@Data
@NoArgsConstructor
public class SaldoUtilizador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_saldo_utilizador",columnDefinition = "UUID")
    private UUID idSaldoUtilizador;
    
    private String nome;
    
    @Column(name = "pontos_perdidos")
    private Integer pontosPerdidos;
    
    @Column(name = "pontos_ganhos")
    private Integer pontosGanhos;
    
    private Integer saldo;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
    
    
   @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_utilizador")
    private Utilizador utilizador;

     public Integer getSaldoParcial() {
        return this.saldo;
    }
    
    public void setSaldoParcial(Integer saldo) {
        this.saldo = saldo;
    }


    public Integer getPontosGastos() {
        return this.pontosPerdidos;
    }
    
    public void setPontosGastos(Integer pontos) {
        this.pontosPerdidos = pontos;
    }
}