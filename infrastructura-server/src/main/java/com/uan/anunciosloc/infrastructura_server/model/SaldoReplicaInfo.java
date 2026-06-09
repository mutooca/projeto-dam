package com.uan.anunciosloc.infrastructura_server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaldoReplicaInfo {

    private String idUtilizador;

    private float saldo;

    
    // quando o anunciosloc_server lê de N réplicas, escolhe a de versão mais alta
    private int versao;

    private LocalDateTime actualizadoEm;
}
