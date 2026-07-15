package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilizadorRepository extends JpaRepository<Utilizador, UUID> {
  Optional<Utilizador> findByEmail(String email);

  boolean existsByEmail(String email);

  /*
   * *
   * 
   * @Query("SELECT COUNT(u) FROM Utilizador u WHERE u.ultimoPost >= :dataLimite")
   * long countAtivos(@Param("dataLimite") LocalDateTime dataLimite);
   * 
   * @Query("SELECT COUNT(u) FROM Utilizador u WHERE u.ultimoPost IS NULL OR u.ultimoPost < :dataLimite"
   * )
   * long countInativos(@Param("dataLimite") LocalDateTime dataLimite);
   * 
   * 
   * @Query("SELECT u FROM Utilizador u WHERE u.ultimoPost IS NULL OR u.ultimoPost < :dataLimite"
   * )
   * List<Utilizador> findInativos(@Param("dataLimite") LocalDateTime dataLimite);
   * 
   * 
   * @Query("SELECT u FROM Utilizador u WHERE u.ultimoPost >= :dataLimite")
   * List<Utilizador> findAtivos(@Param("dataLimite") LocalDateTime dataLimite);
   **/

  List<Utilizador> findByRoleAndAtivoTrue(String role);

  @Query("SELECT u FROM Utilizador u WHERE u.role = 'USER' AND u.saldo > 0")
  List<Utilizador> findUsersWithSaldo();

  @Query("SELECT COUNT(u) FROM Utilizador u WHERE u.role = 'USER'")
  long countByRoleUser();


  List<Utilizador> findByRole(String role);

    long countByRole(String role);

    @Query("SELECT COUNT(u) FROM Utilizador u " +
           "WHERE u.role = 'USER' AND u.dataUltimoPost >= :dataLimite")
    long countActiveUsers(@Param("dataLimite") LocalDateTime dataLimite);

   
    @Query("SELECT COUNT(u) FROM Utilizador u " +
           "WHERE u.role = 'USER' AND (u.dataUltimoPost IS NULL OR u.dataUltimoPost < :dataLimite)")
    long countInactiveUsers(@Param("dataLimite") LocalDateTime dataLimite);

    
    @Query("SELECT u FROM Utilizador u " +
           "WHERE u.role = 'USER' AND u.dataUltimoPost >= :dataLimite")
    List<Utilizador> findActiveUsers(@Param("dataLimite") LocalDateTime dataLimite);

    
    @Query("SELECT u FROM Utilizador u " +
           "WHERE u.role = 'USER' AND (u.dataUltimoPost IS NULL OR u.dataUltimoPost < :dataLimite)")
    List<Utilizador> findInactiveUsers(@Param("dataLimite") LocalDateTime dataLimite);
}
