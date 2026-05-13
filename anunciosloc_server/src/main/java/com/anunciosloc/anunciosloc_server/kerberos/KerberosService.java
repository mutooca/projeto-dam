package com.anunciosloc.anunciosloc_server.kerberos;

import com.anunciosloc.anunciosloc_server.model.KerberosSession;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.KerberosSessionRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KerberosService {
    
    private final UtilizadorRepository utilizadorRepository;
    private final KerberosSessionRepository sessionRepository;
    
    @Value("${kerberos.constante}")
    private String constante;
    
    @Value("${kerberos.hash.algoritmo}")
    private String hashAlgoritmo;
    
    private static final int SESSION_DURATION_HOURS = 24;
    
    /**
     * PASSO 1: Cliente pede novo ticket (TGT)
     * Recebe: email do utilizador
     * Retorna: Ticket + Chave de sessão (criptografada com chave do user)
     */
    @Transactional
    public KerberosTicket requestTicket(String email, String clientNonce) {
        // Verificar se utilizador existe
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
        
        // Gerar chave de sessão
        String sessionKey = KerberosCryptoUtil.generateSessionKey();
        String sessionId = UUID.randomUUID().toString();
        
        // Criar sessão no banco de dados
        KerberosSession session = new KerberosSession();
        session.setSessionId(sessionId);
        session.setEmailUtilizador(email);
        session.setSessionKey(sessionKey);  // Na prática, deveria ser criptografado
        session.setCriadoEm(LocalDateTime.now());
        session.setExpiraEm(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS));
        session.setAtivo(true);
        sessionRepository.save(session);
        
        // Criar ticket
        KerberosTicket ticket = KerberosTicket.builder()
            .sessionId(sessionId)
            .emailUtilizador(email)
            .sessionKey(sessionKey)
            .criadoEm(LocalDateTime.now())
            .expiraEm(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS))
            .serviceName("AnunciosLoc")
            .build();
        
        // TODO: Criptografar ticket com chave do utilizador
        // Por simplicidade nesta implementação, não criptografamos
        
        return ticket;
    }
    
    /**
     * PASSO 2: Cliente encripta autenticador com chave de sessão
     * Servidor valida ticket + autenticador
     */
    @Transactional
    public boolean validateTicketAndAuthenticator(String serializedTicket, String serializedAuth) {
        // Deserializar ticket
        KerberosTicket ticket = KerberosTicket.deserialize(serializedTicket);
        KerberosAuthenticator authenticator = KerberosAuthenticator.deserialize(serializedAuth);
        
        // Buscar sessão no banco
        KerberosSession session = sessionRepository.findBySessionIdAndAtivoTrue(ticket.getSessionId())
            .orElseThrow(() -> new RuntimeException("Sessão inválida ou expirada"));
        
        // Verificar se sessão não expirou
        if (session.getExpiraEm().isBefore(LocalDateTime.now())) {
            session.setAtivo(false);
            sessionRepository.save(session);
            throw new RuntimeException("Sessão expirada");
        }
        
        // Verificar se o email do ticket coincide com o autenticador
        if (!ticket.getEmailUtilizador().equals(authenticator.getEmailUtilizador())) {
            throw new RuntimeException("Ticket não corresponde ao autenticador");
        }
        
        // Verificar MAC do autenticador
        String authDataForMAC = authenticator.getEmailUtilizador() + "|" + 
                                authenticator.getTimestamp() + "|" + 
                                authenticator.getNonce();
        
        if (!KerberosCryptoUtil.verifyMAC(authDataForMAC, session.getSessionKey(), authenticator.getMac())) {
            throw new RuntimeException("MAC inválido - integridade comprometida");
        }
        
        // Verificar timestamp (prevenir replay attacks)
        LocalDateTime authTime = LocalDateTime.parse(authenticator.getTimestamp());
        if (authTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new RuntimeException("Autenticador expirado (timestamp muito antigo)");
        }
        
        return true;
    }
    
    /**
     * Gerar prova de frescura (resposta do servidor)
     */
    public String generateFreshnessProof(String sessionId, String clientNonce) {
        KerberosSession session = sessionRepository.findBySessionIdAndAtivoTrue(sessionId)
            .orElseThrow(() -> new RuntimeException("Sessão inválida"));
        
        String responseData = clientNonce + "|" + KerberosCryptoUtil.getCurrentTimestamp();
        return KerberosCryptoUtil.calculateMAC(responseData, session.getSessionKey());
    }
    
    /**
     * Encerrar sessão (logout)
     */
    @Transactional
    public void logout(String sessionId) {
        sessionRepository.deleteBySessionId(sessionId);
    }
    
    /**
     * Verificar se uma sessão é válida
     */
    public boolean isSessionValid(String sessionId) {
        return sessionRepository.findBySessionIdAndAtivoTrue(sessionId)
            .map(session -> session.getExpiraEm().isAfter(LocalDateTime.now()))
            .orElse(false);
    }
}