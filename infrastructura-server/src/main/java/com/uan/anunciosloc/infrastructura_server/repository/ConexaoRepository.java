package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uan.anunciosloc.infrastructura_server.model.Conexao;

public interface ConexaoRepository extends JpaRepository<Conexao, UUID> {

    Optional<Conexao> findByIdUtilizador(UUID idUtilizador);

    Optional<Conexao> findByIdInfraestruturaAndIdUtilizador(UUID idInfraestrutura, UUID idUtilizador);

    List<Conexao> findByIdInfraestrutura(UUID idInfraestrutura);

    long countByEstado(String estado);
}