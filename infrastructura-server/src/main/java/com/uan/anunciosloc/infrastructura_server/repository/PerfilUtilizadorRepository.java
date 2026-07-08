package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.uan.anunciosloc.infrastructura_server.model.PerfilUtilizador;

public interface PerfilUtilizadorRepository extends JpaRepository<PerfilUtilizador, UUID> {

    List<PerfilUtilizador> findByIdUtilizador(UUID idUtilizador);

    boolean existsByIdUtilizadorAndChaveAndValor(UUID idUtilizador, String chave, String valor);

    
    @Query("SELECT p FROM PerfilUtilizador p WHERE p.email = :email")
    List<PerfilUtilizador> findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    void deleteByIdUtilizador(UUID idUtilizador);
}