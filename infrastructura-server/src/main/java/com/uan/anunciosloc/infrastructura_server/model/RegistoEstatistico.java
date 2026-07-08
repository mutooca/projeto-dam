package com.uan.anunciosloc.infrastructura_server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registo_estatistico")
@Data
@NoArgsConstructor
public class RegistoEstatistico {
    @Id
    private Long idRegistoEstatistico;

    private Integer totalAnuncio;
    private Integer totalEntrega;
    private Integer totalConexao;
    private LocalDate dataRegisto;
    private LocalDateTime actualizadoEm;

    @Column(name = "id_infraestrutura")
    private UUID idInfraestrutura;
}
