package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uan.anunciosloc.infrastructura_server.model.Infraestrutura;

public interface InfraestruturaRepository
        extends JpaRepository<Infraestrutura, UUID> {
    Optional<Infraestrutura> findByNome(String nome);
    

    @Query("SELECT i FROM Infraestrutura i WHERE i.ativa = true")
    Optional<Infraestrutura> findInfraAtiva();

    @Query("SELECT i FROM Infraestrutura i WHERE i.idInfraestrutura = :id AND i.ativa = true")
    Optional<Infraestrutura> findByIdAndAtivaTrue(@Param("id") UUID id);

    boolean existsByNome(String nome);
}
