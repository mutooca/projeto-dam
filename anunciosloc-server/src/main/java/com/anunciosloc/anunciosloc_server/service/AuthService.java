package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.client.TicketResponseAdmin;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import lombok.extern.slf4j.Slf4j;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.util.EmailValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
//import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final KerberosSoapClient kerberosClient;
    private final UtilizadorRepository utilizadorRepository;
    private final ObjectMapper objectMapper;
     private final QuorumService quorumService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Utilizador registar(RegistarUtilizadorRequest request) {
        
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new RuntimeException("Email inválido. Domínios permitidos: gmail.com, hotmail.com, etc.");
        }
        
        if (utilizadorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já registado");
        }
        
        
        Utilizador user = new Utilizador();
        user.setEmail(request.getEmail());
        //user.setPalavraChave(request.getPalavraChave());
        user.setPalavraChave(passwordEncoder.encode(request.getPalavraChave()));
        user.setNome(request.getNome());
        user.setPreferenciaAnuncio(request.getPreferenciaAnuncio());
        user.setSaldo(10);
        user.setRole("USER");
        user.setDataCriacao(LocalDateTime.now());
        user.setAtivo(true);

        

        utilizadorRepository.save(user);
        
      
        try {
            String responseJson = kerberosClient.criarUtilizador(request.getEmail(), request.getPalavraChave());
            System.out.println(" Resposta do Kerberos (criação): " + responseJson);
        } catch (Exception e) {
            utilizadorRepository.delete(user);
            throw new RuntimeException("Erro ao criar utilizador no Kerberos: " + e.getMessage());
        }

        try {
            quorumService.escreverSaldoQuorum(
                user.getEmail(),
                user.getSaldo(),1  
            );
            log.info(" Saldo inicial (10) replicado para o Infra-Server");
        } catch (Exception e) {
            log.warn(" Erro ao replicar saldo inicial: {}", e.getMessage());
            
        }

        return user;
    }
        
       

    @SuppressWarnings("unchecked")
    public TicketResponse login(String email, String password, String clientNonce) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (!passwordEncoder.matches(password, user.getPalavraChave())) {
            throw new RuntimeException("Palavra Chave incorrecta");
        }
        
        if (!user.isAtivo()) {
            throw new RuntimeException("Utilizador não ativado");
        }
        
        boolean existeNoKerberos = false;
        try {
            existeNoKerberos = kerberosClient.verificarUtilizador(email);
            System.out.println("Utilizador existe no Kerberos? " + existeNoKerberos);
        } catch (Exception e) {
            System.out.println("Erro ao verificar utilizador no Kerberos: " + e.getMessage());
        }
        
        if (!existeNoKerberos) {
            try {
                String responseJson = kerberosClient.criarUtilizador(email, password);
                System.out.println("Utilizador criado no Kerberos automaticamente: " + responseJson);
            } catch (Exception e) {
                System.out.println("Erro ao criar utilizador no Kerberos: " + e.getMessage());
                
            }
        }
        
        
        try {
            String responseJson = kerberosClient.requestTicket(email, clientNonce);
            System.out.println("Resposta do Kerberos (requestTicket): " + responseJson);
            
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

    @SuppressWarnings("unchecked")
    public TicketResponseAdmin loginAdmin(String email, String password, String clientNonce) {
        
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (!passwordEncoder.matches(password, user.getPalavraChave())) {
            throw new RuntimeException("Palavra Chave incorrecta");
        }
        
        if (!user.isAtivo()) {
            throw new RuntimeException("Utilizador não ativado");
        }
    
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Acesso negado: esta conta não tem privilégios de administrador");
        }
        boolean existeNoKerberos = false;
        try {
            existeNoKerberos = kerberosClient.verificarUtilizador(email);
            System.out.println("Utilizador existe no Kerberos? " + existeNoKerberos);
        } catch (Exception e) {
            System.out.println("Erro ao verificar utilizador no Kerberos: " + e.getMessage());
        }
        
        if (!existeNoKerberos) {
            try {
                String responseJson = kerberosClient.criarUtilizador(email, password);
                System.out.println("Utilizador criado no Kerberos automaticamente: " + responseJson);
            } catch (Exception e) {
                System.out.println("Erro ao criar utilizador no Kerberos: " + e.getMessage());
               
            }
        }
        try {
            String responseJson = kerberosClient.requestTicket(email, clientNonce);
            System.out.println("Resposta do Kerberos (requestTicket): " + responseJson);
            
            Map<String, Object> map = objectMapper.readValue(responseJson, Map.class);
            
            TicketResponseAdmin response = new TicketResponseAdmin();
            response.setSuccess((Boolean) map.get("success"));
            response.setTicket((String) map.get("ticket"));
            response.setSessionKey((String) map.get("sessionKey"));
            response.setSessionId((String) map.get("sessionId"));
            response.setMessage((String) map.get("message"));
            response.setRole(user.getRole());  // ← ADICIONAR ROLE
        
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com Kerberos: " + e.getMessage());
        }
    }

    public void logout(String sessionId) {
        try {
            kerberosClient.logout(sessionId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer logout: " + e.getMessage());
        }
    }
}