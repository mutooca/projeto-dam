package com.anunciosloc.kerberos.service;

import com.anunciosloc.kerberos.crypto.KerberosCryptoUtil;
import com.anunciosloc.kerberos.model.KerberosSession;
import com.anunciosloc.kerberos.model.Utilizador;
import com.anunciosloc.kerberos.repository.KerberosSessionRepository;
import com.anunciosloc.kerberos.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KerberosService {
    
    private final UtilizadorRepository utilizadorRepository;
    private final KerberosSessionRepository sessionRepository;
    
    @Value("${kerberos.constante}")
    private String constante;
    
    @Value("${kerberos.token.validade.horas}")
    private int validadeHoras;
    
    @Transactional
    public TicketData requestTicket(String email, String clientNonce) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
        
        if (!user.isAtivo()) {
            throw new RuntimeException("Utilizador não está ativo");
        }
        
        String sessionId = UUID.randomUUID().toString();
        String sessionKey = KerberosCryptoUtil.generateSessionKey();
        
        KerberosSession session = new KerberosSession();
        session.setSessionId(sessionId);
        session.setEmailUtilizador(email);
        session.setSessionKey(sessionKey);
        session.setCriadoEm(LocalDateTime.now());
        session.setExpiraEm(LocalDateTime.now().plusHours(validadeHoras));
        session.setAtivo(true);
        sessionRepository.save(session);
        
        // Criar ticket (serializado)
        String ticketData = sessionId + "|" + email + "|" + sessionKey + "|" + 
                            session.getCriadoEm() + "|" + session.getExpiraEm() + "|AnunciosLoc";
        String ticket = Base64.getEncoder().encodeToString(ticketData.getBytes());
        
        return new TicketData(ticket, sessionKey, sessionId);
    }
    
    @Transactional
    public boolean validateTicketAndAuthenticator(String serializedTicket, String serializedAuthenticator) {
        // Deserializar ticket
        String ticketDecoded = new String(Base64.getDecoder().decode(serializedTicket));
        String[] ticketParts = ticketDecoded.split("\\|");
        String sessionId = ticketParts[0];
        String emailFromTicket = ticketParts[1];
        String sessionKey = ticketParts[2];
        
        // Deserializar autenticador
        String authDecoded = new String(Base64.getDecoder().decode(serializedAuthenticator));
        String[] authParts = authDecoded.split("\\|");
        String emailFromAuth = authParts[0];
        String timestamp = authParts[1];
        String nonce = authParts[2];
        String mac = authParts[3];
        
        // Buscar sessão
        KerberosSession session = sessionRepository.findBySessionIdAndAtivoTrue(sessionId)
            .orElseThrow(() -> new RuntimeException("Sessão inválida ou expirada"));
        
        // Verificar correspondência
        if (!emailFromTicket.equals(emailFromAuth)) {
            return false;
        }
        
        // Verificar MAC
        String dataForMAC = emailFromAuth + "|" + timestamp + "|" + nonce;
        if (!KerberosCryptoUtil.verifyMAC(dataForMAC, session.getSessionKey(), mac)) {
            return false;
        }
        
        // Verificar timestamp (5 minutos)
        LocalDateTime authTime = LocalDateTime.parse(timestamp);
        if (authTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            return false;
        }
        
        return true;
    }
    
    @Transactional
    public void logout(String sessionId) {
        sessionRepository.deactivateSession(sessionId);
    }
    
    @Transactional
    public void logoutAll(String email) {
        List<KerberosSession> sessions = sessionRepository.findByEmailUtilizadorAndAtivoTrue(email);
        for (KerberosSession session : sessions) {
            session.setAtivo(false);
            sessionRepository.save(session);
        }
    }
    
    public boolean isSessionValid(String sessionId) {
        return sessionRepository.findBySessionIdAndAtivoTrue(sessionId)
            .map(session -> session.getExpiraEm().isAfter(LocalDateTime.now()))
            .orElse(false);
    }
    
    // Classe interna para retorno
    public record TicketData(String ticket, String sessionKey, String sessionId) {}
}