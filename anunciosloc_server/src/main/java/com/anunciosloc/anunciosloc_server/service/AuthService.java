package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.util.EmailValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KerberosSoapClient kerberosClient;
    private final UtilizadorRepository utilizadorRepository;
    private final ObjectMapper objectMapper;

   @SuppressWarnings("unchecked")
public TicketResponse login(String email, String clientNonce) {
    System.out.println("AuthService.login: " + email);
    
    Utilizador user = utilizadorRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    
    if (!user.isAtivo()) {
        throw new RuntimeException("Utilizador não ativado");
    }
    
    try {
        String responseJson = kerberosClient.requestTicket(email, clientNonce);
        System.out.println(" Resposta do Kerberos (requestTicket): " + responseJson);
        
        Map<String, Object> map = objectMapper.readValue(responseJson, Map.class);
        
        TicketResponse response = new TicketResponse();
        response.setSuccess((Boolean) map.get("success"));
        response.setTicket((String) map.get("ticket"));
        response.setSessionKey((String) map.get("sessionKey"));
        response.setSessionId((String) map.get("sessionId"));
        response.setMessage((String) map.get("message"));
        
        System.out.println(" Ticket gerado: " + response.getTicket());
        System.out.println(" SessionID gerado: " + response.getSessionId());
        
        return response;
    } catch (Exception e) {
        throw new RuntimeException("Erro ao comunicar com Kerberos: " + e.getMessage());
    }
}


    @Transactional
public Utilizador registar(RegistarUtilizadorRequest request) {
    
    if (!EmailValidator.isValid(request.getEmail())) {
        throw new RuntimeException("Email inválido. Domínios permitidos: gmail.com, hotmail.com, etc.");
    }
    
    
    if (utilizadorRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email já registado");
    }
    
   
    Utilizador user = new Utilizador();
    user.setNome(request.getNome());
    user.setEmail(request.getEmail());
    user.setPalavraChave(request.getPalavraChave());
    user.setSaldo(10);
    user.setRole(request.getRole() != null ? request.getRole() : "USER");
    user.setDataCriacao(LocalDateTime.now());
    user.setAtivo(true);
    utilizadorRepository.save(user);
    
    
    try {
        String responseJson = kerberosClient.criarUtilizador(request.getEmail(), request.getPalavraChave());
        System.out.println(" Resposta do Kerberos: " + responseJson);
        
        // Verificar se a resposta indica sucesso
        if (responseJson.contains("\"success\":false")) {
            utilizadorRepository.delete(user);  // Rollback
            throw new RuntimeException("Kerberos rejeitou a criação: " + responseJson);
        }
        
        System.out.println(" Utilizador criado no Kerberos: " + request.getEmail());
    } catch (Exception e) {
        utilizadorRepository.delete(user);  // Rollback
        throw new RuntimeException("Erro ao criar utilizador no Kerberos: " + e.getMessage());
    }
    
    return user;
}


    public void logout(String sessionId) {
    try {
        kerberosClient.logout(sessionId);
    } catch (Exception e) {
        throw new RuntimeException("Erro ao fazer logout: " + e.getMessage());
    }
}
}