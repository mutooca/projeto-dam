package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uan.anunciosloc.infrastructura_server.model.SaldoUtilizador;

public interface SaldoUtilizadorRepository extends JpaRepository<SaldoUtilizador, UUID> {

    List<SaldoUtilizador> findByEmailUtilizador(String emailUtilizador);

    @Query("SELECT s FROM SaldoUtilizador s WHERE s.emailUtilizador = :email AND s.idInfraestrutura = :infraId")
    Optional<SaldoUtilizador> findByEmailUtilizadorAndIdInfraestrutura(
            @Param("email") String email,
            @Param("infraId") UUID infraId);

    @Query("SELECT s FROM SaldoUtilizador s " +
           "WHERE s.idInfraestrutura = :idInfra " +
           "AND s.saldoParcial > 0")
    List<SaldoUtilizador> findComSaldoPorInfra(@Param("idInfra") UUID idInfra);
}