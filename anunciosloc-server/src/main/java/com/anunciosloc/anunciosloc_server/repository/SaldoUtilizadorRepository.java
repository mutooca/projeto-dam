package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.SaldoUtilizador;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SaldoUtilizadorRepository extends JpaRepository<SaldoUtilizador, UUID> {

    Optional<SaldoUtilizador> findByUtilizador(Utilizador utilizador);
}
