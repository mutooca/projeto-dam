package com.anunciosloc.anunciosloc_server.repository;



import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InfraestruturaRepository extends JpaRepository<Infraestrutura, UUID> {

    List<Infraestrutura> findByGestor(Utilizador gestor);
    Optional<Infraestrutura> findByNome(String nome);
    List<Infraestrutura> findByAtivaTrue();
    Optional<Infraestrutura> findByIdInfraestrutura(UUID id);
}
