package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uan.anunciosloc.infrastructura_server.model.Anuncio;

public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

    List<Anuncio> findByIdLocalAndEstado(UUID idLocal, String estado);

    @Query("SELECT a FROM Anuncio a WHERE a.idLocal = :localId AND a.estado = 'ATIVO' " +
           "AND (a.visivelDe IS NULL OR a.visivelDe <= CURRENT_TIMESTAMP) " +
           "AND (a.visivelAte IS NULL OR a.visivelAte >= CURRENT_TIMESTAMP)")
    List<Anuncio> findAtivosByLocal(@Param("localId") UUID localId);
    List<Anuncio> findByAutorEmail(String autorEmail);

    List<Anuncio> findByIdInfraestrutura(UUID idInfraestrutura);
}