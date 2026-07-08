package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conexao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conexao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_conexao")
    private UUID idConexao;

    @Column(name = "id_utilizador", nullable = false)
    private UUID idUtilizador;

    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @Column(name = "data_conexao")
    private LocalDateTime dataConexao;

    @Column(name = "estado")
    private String estado;  
}