package com.anunciosloc.anunciosloc_server.repository;



import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilizadorRepository extends JpaRepository<Utilizador, UUID> {
    Optional<Utilizador> findByEmail(String email);
    boolean existsByEmail(String email);

  /* *
    @Query("SELECT COUNT(u) FROM Utilizador u WHERE u.ultimoPost >= :dataLimite")
    long countAtivos(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("SELECT COUNT(u) FROM Utilizador u WHERE u.ultimoPost IS NULL OR u.ultimoPost < :dataLimite")
    long countInativos(@Param("dataLimite") LocalDateTime dataLimite);

    
    @Query("SELECT u FROM Utilizador u WHERE u.ultimoPost IS NULL OR u.ultimoPost < :dataLimite")
    List<Utilizador> findInativos(@Param("dataLimite") LocalDateTime dataLimite);

    
    @Query("SELECT u FROM Utilizador u WHERE u.ultimoPost >= :dataLimite")
    List<Utilizador> findAtivos(@Param("dataLimite") LocalDateTime dataLimite);**/

  
    List<Utilizador> findByRole(String role);
}
