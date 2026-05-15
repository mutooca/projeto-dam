package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KerberosSoapClient kerberosClient;
    private final UtilizadorRepository utilizadorRepository;
    private final ObjectMapper objectMapper;

    public TicketResponse login(String email, String clientNonce) {
         System.out.println("AuthService.login: " + email);
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (!user.isAtivo()) {
            throw new RuntimeException("Utilizador não ativado");
        }
        
        try {
            String responseJson = kerberosClient.requestTicket(email, clientNonce);
            Map<String, Object> map = objectMapper.readValue(responseJson, Map.class);
            
            TicketResponse response = new TicketResponse();
            response.setSuccess((Boolean) map.get("success"));
            response.setTicket((String) map.get("ticket"));
            response.setSessionKey((String) map.get("sessionKey"));
            response.setSessionId((String) map.get("sessionId"));
            response.setMessage((String) map.get("message"));
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com Kerberos: " + e.getMessage());
        }
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