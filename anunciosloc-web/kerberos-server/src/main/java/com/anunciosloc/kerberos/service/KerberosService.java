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
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KerberosService {
    
    private final UtilizadorRepository utilizadorRepository;
    private final KerberosSessionRepository sessionRepository;
    private static final DateTimeFormatter DATE_FORMATTER = 
    DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
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

        String criadoEmStr = session.getCriadoEm().format(DATE_FORMATTER);
        String expiraEmStr = session.getExpiraEm().format(DATE_FORMATTER);
        
        // Criar ticket (serializado)
        String ticketData = sessionId + "|" + email + "|" + sessionKey + "|" + 
                            criadoEmStr + "|" + expiraEmStr + "|AnunciosLoc";

        System.out.println("Ticket gerado com datas formatadas:");
        System.out.println("   Criado em: " + criadoEmStr);
        System.out.println("   Expira em: " + expiraEmStr);

        String ticket = Base64.getEncoder().encodeToString(ticketData.getBytes());
        
        return new TicketData(ticket, sessionKey, sessionId);
    }
    
    @Transactional
public boolean validateTicketAndAuthenticator(String serializedTicket, 
                                               String serializedAuthenticator) {
    System.out.println("═══════════════════════════════════════════════════════════");
    System.out.println("KERBEROS: Iniciando validação de ticket + autenticador");
    System.out.println("═══════════════════════════════════════════════════════════");
    
    
    String ticketDecoded = new String(Base64.getDecoder().decode(serializedTicket));
    String[] ticketParts = ticketDecoded.split("\\|");
    
    System.out.println("TICKET:");
    System.out.println("   - SessionId: " + ticketParts[0]);
    System.out.println("   - Email: " + ticketParts[1]);
    System.out.println("   - SessionKey (primeiros 20 chars): " + 
                       (ticketParts[2].length() > 20 ? ticketParts[2].substring(0, 20) + "..." : ticketParts[2]));
    System.out.println("   - Criado em: " + ticketParts[3]);
    System.out.println("   - Expira em: " + ticketParts[4]);

    String sessionId       = ticketParts[0];
    String emailFromTicket = ticketParts[1];
    LocalDateTime expiraEm = LocalDateTime.parse(ticketParts[4], DATE_FORMATTER);

   
    LocalDateTime agora = LocalDateTime.now();
    if (agora.isAfter(expiraEm)) {
        System.out.println("FALHOU: Ticket expirado!");
        System.out.println("   Expira em: " + expiraEm);
        System.out.println("   Agora: " + agora);
        return false;
    }
    System.out.println("Ticket válido (não expirado)");

    
    String authDecoded = new String(Base64.getDecoder().decode(serializedAuthenticator));
    String[] authParts = authDecoded.split("\\|");
    
    System.out.println("\nAUTHENTICATOR:");
    System.out.println("   - Email: " + authParts[0]);
    System.out.println("   - Timestamp: " + authParts[1]);
    System.out.println("   - Nonce: " + authParts[2]);
    System.out.println("   - MAC (primeiros 20 chars): " + 
                       (authParts[3].length() > 20 ? authParts[3].substring(0, 20) + "..." : authParts[3]));

    String emailFromAuth = authParts[0];
    String timestamp     = authParts[1];
    String nonce         = authParts[2];
    String mac           = authParts[3];

   
    if (!emailFromTicket.equals(emailFromAuth)) {
        System.out.println("FALHOU: Emails diferentes!");
        System.out.println("   Email do ticket: " + emailFromTicket);
        System.out.println("   Email do authenticator: " + emailFromAuth);
        return false;
    }
    System.out.println(" Email corresponde: " + emailFromTicket);

    
    KerberosSession session = sessionRepository
            .findBySessionIdAndAtivoTrue(sessionId)
            .orElseThrow(() -> new RuntimeException("Sessão inválida ou expirada"));

    System.out.println("\n SESSÃO:");
    System.out.println("   - SessionId: " + session.getSessionId());
    System.out.println("   - Email: " + session.getEmailUtilizador());
    System.out.println("   - Criado em: " + session.getCriadoEm());
    System.out.println("   - Expira em: " + session.getExpiraEm());
    System.out.println("   - Ativa: " + session.isAtivo());

    
    String dataForMAC = emailFromAuth + "|" + timestamp + "|" + nonce;
    String calculatedMAC = KerberosCryptoUtil.calculateMAC(dataForMAC, session.getSessionKey());
    
    System.out.println("\n MAC:");
    System.out.println("   - Dados para MAC: " + dataForMAC);
    System.out.println("   - MAC recebido: " + mac);
    System.out.println("   - MAC calculado: " + calculatedMAC);
    System.out.println("   - MACs iguais? " + mac.equals(calculatedMAC));

    if (!KerberosCryptoUtil.verifyMAC(dataForMAC, session.getSessionKey(), mac)) {
        System.out.println("FALHOU: MAC inválido!");
        return false;
    }
    System.out.println("MAC válido");

    
    LocalDateTime authTime = LocalDateTime.parse(timestamp);
    LocalDateTime agoraVal = LocalDateTime.now();
    long secondsDiff = java.time.Duration.between(authTime, agoraVal).getSeconds();
    long minutesDiff = secondsDiff / 60;
    long remainingSeconds = secondsDiff % 60;
    
    System.out.println("\n  TIMESTAMP:");
    System.out.println("   - Timestamp do authenticator: " + authTime);
    System.out.println("   - Hora atual: " + agoraVal);
    System.out.println("   - Diferença: " + minutesDiff + " minutos e " + remainingSeconds + " segundos");
    
    if (authTime.isBefore(agoraVal.minusMinutes(5))) {
        System.out.println("FALHOU: Autenticador expirado! (limite 5 minutos)");
        System.out.println("   Diferença de " + minutesDiff + " minutos excede o limite de 5 minutos");
        return false;
    }
    System.out.println("Timestamp válido (dentro do limite de 5 minutos)");

    
    System.out.println("\n═══════════════════════════════════════════════════════════");
    System.out.println(" VALIDAÇÃO BEM SUCEDIDA!");
    System.out.println("═══════════════════════════════════════════════════════════\n");
    
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