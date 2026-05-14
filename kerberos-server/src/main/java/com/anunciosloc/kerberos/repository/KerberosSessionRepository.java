package com.anunciosloc.kerberos.repository;

import com.anunciosloc.kerberos.model.KerberosSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface KerberosSessionRepository extends JpaRepository<KerberosSession, Long> {
    Optional<KerberosSession> findBySessionIdAndAtivoTrue(String sessionId);
    List<KerberosSession> findByEmailUtilizadorAndAtivoTrue(String email);
    
    @Modifying
    @Transactional
    @Query("UPDATE KerberosSession s SET s.ativo = false WHERE s.sessionId = :sessionId")
    void deactivateSession(String sessionId);
    
    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);
}