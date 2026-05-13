package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.KerberosSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface KerberosSessionRepository extends JpaRepository<KerberosSession, Long> {
    //Optional<KerberosSession> findBySessionIdAndAtivoTrue(String sessionId);
    //Optional<KerberosSession> findByEmailUtilizadorAndAtivoTrue(String email);
    //void deleteBySessionId(String sessionId);

    Optional<KerberosSession> findBySessionIdAndAtivoTrue(String sessionId);
    
    List<KerberosSession> findByEmailUtilizadorAndAtivoTrue(String email);
    
    @Modifying
    @Transactional
    @Query("UPDATE KerberosSession s SET s.ativo = false WHERE s.sessionId = :sessionId")
    void deactivateSession(@Param("sessionId") String sessionId);
    
    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);
}