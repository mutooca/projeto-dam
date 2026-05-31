package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "infraestrutura")
@Data
@NoArgsConstructor
public class Infraestrutura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_infraestrutura", columnDefinition = "UUID")
    private UUID idInfraestrutura;

    @Column(nullable = false)  
    private String nome; 
    
    private Integer capacidade;
    
    @Column(name = "bonus_entrega")
    private Integer bonusEntrega;
    
    @Column(name = "custo_post")
    private Integer custoPost;  
    
    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;
    
    private boolean ativa;
    
    // Estatísticas
    @Column(name = "total_anuncios")
    private Integer totalAnuncios = 0;
    
    @Column(name = "total_entregas")
    private Integer totalEntregas = 0;
    
    @Column(name = "total_conexoes")
    private Integer totalConexoes = 0;
    
   
    
    @ManyToOne
    @JoinColumn(name = "id_gestor", referencedColumnName = "id_utilizador")
    private Utilizador gestor;
    
    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL)
    private List<Local> locais = new ArrayList<>();
    
    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL)
    private List<SaldoUtilizador> saldos = new ArrayList<>();
    
    @OneToMany(mappedBy = "infraestrutura")
    private List<Conexao> conexoes = new ArrayList<>();
    
    @OneToMany(mappedBy = "infraestrutura", cascade = CascadeType.ALL)
    private List<Restricao> restricoes = new ArrayList<>();
    
    @OneToOne(mappedBy = "infraestrutura", cascade = CascadeType.ALL)
    private RegistoEstatistico registoEstatistico;
    
    @OneToMany(mappedBy = "infraestrutura")
    private List<Anuncio> anuncios = new ArrayList<>();
}