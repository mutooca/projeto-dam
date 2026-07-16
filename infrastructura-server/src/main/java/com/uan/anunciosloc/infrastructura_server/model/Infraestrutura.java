package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "infraestrutura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Infraestrutura {

    @Id
    @Column(name = "id_infraestrutura", nullable = false)
    private UUID idInfraestrutura;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "url_endpoint", nullable = false)
    private String urlEndpoint;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private Double raio;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(name = "bonus_entrega")
    private Integer bonusEntrega;

    @Column(name = "custo_post")
    private Integer custoPost;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;

    private boolean ativa;

    // ⭐ ESTATÍSTICAS OPERACIONAIS ⭐
    @Column(name = "total_locais")
    @Builder.Default
    private Integer totalLocais = 0;

    @Column(name = "total_anuncios")
    @Builder.Default
    private Integer totalAnuncios = 0;

    @Column(name = "total_entregas")
    @Builder.Default
    private Integer totalEntregas = 0;

    @Column(name = "total_conexoes")
    @Builder.Default
    private Integer totalConexoes = 0;


}
