package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.KerberosRequest;
import com.anunciosloc.anunciosloc_server.dto.KerberosResponse;
import com.anunciosloc.anunciosloc_server.dto.RegistoRequest;
import com.anunciosloc.anunciosloc_server.kerberos.KerberosService;
import com.anunciosloc.anunciosloc_server.kerberos.KerberosTicket;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    private final UtilizadorRepository utilizadorRepository;
    
    
    @PostMapping("/registar")
    public ResponseEntity<?> registar(@RequestBody RegistoRequest request) {
        try {
            
            if (utilizadorRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email já registado");
            }
            
            Utilizador user = new Utilizador();
            user.setEmail(request.getEmail());
            user.setPasswordHash(request.getPassword());
            user.setSaldo(10);
            user.setRole(request.getRole() != null ? request.getRole() : "USER");
            user.setDataRegisto(LocalDateTime.now());
            user.setAtivo(true);
            
            utilizadorRepository.save(user);
            
            return ResponseEntity.ok("Utilizador registado com sucesso: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KerberosRequest request) {
        try {
            
            Utilizador user = utilizadorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
            
            if (!user.isAtivo()) {
                return ResponseEntity.status(401).body("Utilizador não ativado");
            }
            
            // Pedir ticket Kerberos
            KerberosTicket ticket = authService.kerberosLogin(request.getEmail(), request.getClientNonce());
            
            return ResponseEntity.ok(KerberosResponse.builder()
                .success(true)
                .ticket(ticket.serialize())
                .sessionKey(ticket.getSessionKey())
                .sessionId(ticket.getSessionId())
                .message("Login realizado com sucesso")
                .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}