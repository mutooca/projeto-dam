package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "conexao")
@Data
@NoArgsConstructor
public class Conexao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conexao")
    private Long idConexao;
    
    @Column(name = "data_conexao")
    private LocalDateTime dataConexao;
    
    @ManyToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
    
    
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_utilizador")
    private Utilizador utilizador;
}