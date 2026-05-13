package com.anunciosloc.anunciosloc_server.kerberos;

import com.anunciosloc.anunciosloc_server.dto.KerberosRequest;
import com.anunciosloc.anunciosloc_server.dto.KerberosResponse;
import com.anunciosloc.anunciosloc_server.dto.LogoutRequest;
import com.anunciosloc.anunciosloc_server.model.KerberosSession;
import com.anunciosloc.anunciosloc_server.repository.KerberosSessionRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kerberos")  // (REST para testes)
@RequiredArgsConstructor
public class KerberosController {
    
    private final KerberosService kerberosService;
    private final KerberosSessionRepository sessionRepository;
    
    /**
     * Pedido de novo ticket (TGT)
     */
    @PostMapping("/request-ticket")
    public KerberosResponse requestTicket(@RequestBody KerberosRequest request) {
        try {
            KerberosTicket ticket = kerberosService.requestTicket(
                request.getEmail(), 
                request.getClientNonce()
            );
            
            return KerberosResponse.builder()
                .success(true)
                .ticket(ticket.serialize())
                .sessionKey(ticket.getSessionKey())
                .message("Ticket gerado com sucesso")
                .build();
        } catch (Exception e) {
            return KerberosResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build();
        }
    }
    
    /**
     * Validar ticket e autenticador
     */
    @PostMapping("/validate")
    public KerberosResponse validate(@RequestBody KerberosRequest request) {
        try {
            boolean isValid = kerberosService.validateTicketAndAuthenticator(
                request.getTicket(),
                request.getAuthenticator()
            );
            
            if (isValid) {
                String proof = kerberosService.generateFreshnessProof(
                    request.getSessionId(),
                    request.getClientNonce()
                );
                
                return KerberosResponse.builder()
                    .success(true)
                    .freshnessProof(proof)
                    .message("Autenticação válida")
                    .build();
            } else {
                return KerberosResponse.builder()
                    .success(false)
                    .message("Autenticação inválida")
                    .build();
            }
        } catch (Exception e) {
            return KerberosResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build();
        }
    }
    
    // Logout de uma sessão específica
    @PostMapping("/logout")
    public ResponseEntity<KerberosResponse> logout(@RequestBody LogoutRequest request) {
        try {
            kerberosService.logout(request.getSessionId());
            return ResponseEntity.ok(KerberosResponse.builder()
                .success(true)
                .message("Logout realizado com sucesso")
                .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(KerberosResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build());
        }
    }

      
    @GetMapping("/verify/{sessionId}")
    public ResponseEntity<KerberosResponse> verifySession(@PathVariable String sessionId) {
        boolean isValid = kerberosService.isSessionValid(sessionId);
        return ResponseEntity.ok(KerberosResponse.builder()
            .success(isValid)
            .message(isValid ? "Sessão válida" : "Sessão inválida ou expirada")
            .build());
    }


    
    @PostMapping("/create-authenticator")
    public ResponseEntity<?> createAuthenticator(@RequestBody CreateAuthenticatorRequest request) {
        try {
            
            KerberosSession session = sessionRepository
                .findBySessionIdAndAtivoTrue(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Sessão inválida ou expirada"));
            
            
            if (!session.getEmailUtilizador().equals(request.getEmail())) {
                throw new RuntimeException("Email não corresponde à sessão");
            }
            
            
            String timestamp = LocalDateTime.now().toString();
            
            
            String nonce = UUID.randomUUID().toString();
            
            
            String data = request.getEmail() + "|" + timestamp + "|" + nonce;
            String mac = KerberosCryptoUtil.calculateMAC(data, session.getSessionKey());
            
            
            KerberosAuthenticator authenticator = KerberosAuthenticator.builder()
                .emailUtilizador(request.getEmail())
                .timestamp(timestamp)
                .nonce(nonce)
                .mac(mac)
                .build();
            
        
            Map<String, String> response = new HashMap<>();
            response.put("authenticator", authenticator.serialize());
            response.put("email", request.getEmail());
            response.put("timestamp", timestamp);
            response.put("nonce", nonce);
            response.put("mac", mac);
            response.put("message", "Autenticador gerado com sucesso!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    
}