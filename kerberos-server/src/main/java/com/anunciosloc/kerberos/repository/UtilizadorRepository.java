package com.anunciosloc.kerberos.repository;

import com.anunciosloc.kerberos.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByEmail(String email);
    boolean existsByEmail(String email);
}