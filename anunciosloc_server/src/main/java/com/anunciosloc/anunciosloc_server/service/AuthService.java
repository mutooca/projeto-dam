package com.anunciosloc.anunciosloc_server.service;


import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final KerberosSoapClient kerberosClient;
    private final UtilizadorRepository utilizadorRepository;
    
    public TicketResponse login(String email, String clientNonce) {
        // Verificar se utilizador existe localmente
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (!user.isAtivo()) {
            throw new RuntimeException("Utilizador não ativado");
        }
        
        // Pedir ticket ao servidor Kerberos via SOAP
        return kerberosClient.requestTicket(email, clientNonce);
    }
    
    public Utilizador registar(String email, String password, String role) {
        if (utilizadorRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já registado");
        }
        
        Utilizador user = new Utilizador();
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setSaldo(10);
        user.setRole(role != null ? role : "USER");
        user.setDataRegisto(LocalDateTime.now());
        user.setAtivo(true);
        
        return utilizadorRepository.save(user);
    }
}