package com.anunciosloc.kerberos.controller;

import com.anunciosloc.kerberos.crypto.KerberosCryptoUtil;
import com.anunciosloc.kerberos.model.KerberosSession;
import com.anunciosloc.kerberos.repository.KerberosSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/kerberos")
@CrossOrigin(origins = "*")
public class KerberosTestController {

    @Autowired
    private KerberosSessionRepository sessionRepository;

    @PostMapping("/create-authenticator")
   
public Map<String, String> createAuthenticator(@RequestBody Map<String, String> request) {
    String sessionId = request.get("sessionId");
    String email = request.get("email");
    
    System.out.println("═══════════════════════════════════════════════════════════");
    System.out.println(" KERBEROS: Criando novo authenticator");
    System.out.println("   SessionId: " + sessionId);
    System.out.println("   Email: " + email);
    
    KerberosSession session = sessionRepository.findBySessionIdAndAtivoTrue(sessionId)
        .orElseThrow(() -> new RuntimeException("Sessão inválida"));

    System.out.println("Sessão encontrada - Email: " + session.getEmailUtilizador());
    System.out.println("SessionKey: " + session.getSessionKey());
    
    String timestamp = LocalDateTime.now().toString();
    String nonce = UUID.randomUUID().toString();
    String data = email + "|" + timestamp + "|" + nonce;
    String mac = KerberosCryptoUtil.calculateMAC(data, session.getSessionKey());
    
    String authenticatorData = email + "|" + timestamp + "|" + nonce + "|" + mac;
    String authenticator = Base64.getEncoder().encodeToString(authenticatorData.getBytes(StandardCharsets.UTF_8));
    
    System.out.println("   Timestamp gerado: " + timestamp);
    System.out.println("   Nonce gerado: " + nonce);
    System.out.println("   MAC gerado: " + mac);
    System.out.println("   Authenticator (Base64): " + authenticator);
    System.out.println("Authenticator criado com sucesso!");
    System.out.println("═══════════════════════════════════════════════════════════\n");
    
    Map<String, String> response = new HashMap<>();
    response.put("authenticator", authenticator);
    response.put("email", email);
    response.put("timestamp", timestamp);
    response.put("nonce", nonce);
    response.put("mac", mac);
    return response;
}
}