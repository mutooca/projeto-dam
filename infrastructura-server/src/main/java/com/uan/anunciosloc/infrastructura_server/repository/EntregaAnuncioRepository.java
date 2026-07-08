package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uan.anunciosloc.infrastructura_server.model.EntregaAnuncio;

public interface EntregaAnuncioRepository extends JpaRepository<EntregaAnuncio, UUID> {

    boolean existsByIdAnuncioAndEmailUtilizador(UUID idAnuncio, String emailUtilizador);

    Optional<EntregaAnuncio> findByIdAnuncioAndEmailUtilizador(UUID idAnuncio, String emailUtilizador);

    List<EntregaAnuncio> findByEmailUtilizadorAndEstadoEntrega(String emailUtilizador, String estado);

    @Query("SELECT COUNT(e) > 0 FROM EntregaAnuncio e WHERE e.idAnuncio = :anuncioId AND e.emailUtilizador = :email")
    boolean existsByAnuncioIdAndEmail(@Param("anuncioId") UUID anuncioId, @Param("email") String email);

    List<EntregaAnuncio> findByIdInfraestrutura(UUID idInfraestrutura);
}