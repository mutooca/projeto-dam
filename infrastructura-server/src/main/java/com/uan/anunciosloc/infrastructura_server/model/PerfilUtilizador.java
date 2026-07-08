package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "perfil_utilizador", uniqueConstraints = @UniqueConstraint(columnNames = { "id_utilizador", "chave" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilUtilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil")
    private UUID id;

    @Column(name = "id_utilizador", nullable = false)
    private UUID idUtilizador;

    @Column(nullable = false)
    private String chave;
    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false)
    private String valor;

}