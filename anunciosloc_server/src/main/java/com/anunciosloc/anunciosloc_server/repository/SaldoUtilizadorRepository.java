package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.SaldoUtilizador;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaldoUtilizadorRepository extends JpaRepository<SaldoUtilizador, UUID> {
    Optional<SaldoUtilizador> findByUtilizadorAndInfraestrutura(Utilizador utilizador, Infraestrutura infra);
    List<SaldoUtilizador> findByUtilizador(Utilizador utilizador);
    List<SaldoUtilizador> findByInfraestrutura(Infraestrutura infra);
}