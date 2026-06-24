package com.anunciosloc.anunciosloc_server.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "perfil_utilizador",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"id_utilizador", "chave"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilUtilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_utilizador", nullable = false)
    private Utilizador utilizador;

    // "profissao", "clube", "bairro"
    @Column(nullable = false)
    private String chave;

    // "Estudante", "Real Madrid", "Maianga"
    @Column(nullable = false)
    private String valor;
}