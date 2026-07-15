package com.uan.anunciosloc.infrastructura_server.repository;

import java.time.LocalDateTime;
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

       @Query("SELECT MAX(a.dataPublicacao) FROM Anuncio a WHERE a.autorEmail = :email")
       LocalDateTime findUltimoPostByEmail(@Param("email") String email);

       @Query("SELECT DISTINCT a.autorEmail FROM Anuncio a " +
                     "WHERE a.dataPublicacao >= :dataLimite")
       List<String> findUsuariosComPostRecente(@Param("dataLimite") LocalDateTime dataLimite);

       @Query("SELECT DISTINCT s.emailUtilizador FROM SaldoUtilizador s " +
                     "WHERE s.emailUtilizador NOT IN (" +
                     "    SELECT DISTINCT a.autorEmail FROM Anuncio a " +
                     "    WHERE a.dataPublicacao >= :dataLimite" +
                     ")")
       List<String> findUsuariosInativos(@Param("dataLimite") LocalDateTime dataLimite);

       @Query("SELECT s.emailUtilizador FROM SaldoUtilizador s " +
                     "WHERE s.saldoParcial > 0 " +
                     "AND s.emailUtilizador NOT IN (" +
                     "    SELECT DISTINCT a.autorEmail FROM Anuncio a " +
                     "    WHERE a.dataPublicacao >= :dataLimite" +
                     ")")
       List<String> findUsuariosInativosComSaldo(@Param("dataLimite") LocalDateTime dataLimite);

       List<Anuncio> findByIdInfraestrutura(UUID idInfraestrutura);

       List<Anuncio> findByAutorEmailOrderByDataPublicacaoDesc(String email);

       @Query("SELECT a FROM Anuncio a WHERE a.autorEmail = :email " +
                     "ORDER BY a.dataPublicacao DESC LIMIT 1")
       Anuncio findUltimoAnuncioByEmail(@Param("email") String email);

       @Query("SELECT a FROM Anuncio a WHERE a.autorEmail = :email AND a.estado = 'ATIVO'")
       List<Anuncio> findAtivosByAutorEmail(@Param("email") String email);

       List<Anuncio> findByAutorEmailAndEstado(String autorEmail, String estado);

       List<Anuncio> findByIdLocal(UUID idLocal);

       
       @Query("SELECT COUNT(a) FROM Anuncio a WHERE a.autorEmail = :email")
       long countAnunciosByAutorEmail(@Param("email") String email);
}