package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Entity
@Table(name = "registo_estatistico")
@Data
@NoArgsConstructor
public class RegistoEstatistico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registo_estatistico")
    private Long idRegistoEstatistico;
    
    @Column(name = "total_anuncio")
    private Integer totalAnuncio;
    
    @Column(name = "total_entrega")
    private Integer totalEntrega;
    
    @Column(name = "total_conexao")
    private Integer totalConexao;
    
    @Column(name = "data_registo")
    private LocalDate dataRegisto;
    
    
    @OneToOne
    @JoinColumn(name = "id_infraestrutura")
    private Infraestrutura infraestrutura;
}