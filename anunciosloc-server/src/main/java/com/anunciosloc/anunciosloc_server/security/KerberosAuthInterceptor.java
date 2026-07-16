package com.anunciosloc.anunciosloc_server.security;

import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class KerberosAuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_AUTHENTICATED_EMAIL = "authenticatedEmail";

    private final KerberosSoapClient kerberosClient;
    private final ObjectMapper objectMapper;

    KerberosAuthInterceptor(KerberosSoapClient kerberosClient, ObjectMapper objectMapper) {
        this.kerberosClient = kerberosClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        
        String path = request.getRequestURI();
        System.out.println(" Interceptor - Path: " + path);
        
        if ("OPTIONS".equals(request.getMethod())) {
            System.out.println(" OPTIONS request - permitindo");
            return true;
        }
        
        if (isPublicRoute(path)) {
            System.out.println("Rota pública - permitindo");
            return true;
        }
        
        String ticket = request.getHeader("X-Kerberos-Ticket");
        String authenticator = request.getHeader("X-Kerberos-Authenticator");
        
        System.out.println(" Ticket recebido: " + (ticket != null ? ticket.substring(0, Math.min(50, ticket.length())) + "..." : "null"));
        System.out.println(" Authenticator recebido: " + (authenticator != null ? authenticator.substring(0, Math.min(50, authenticator.length())) + "..." : "null"));
        
        if (ticket == null || authenticator == null) {
            System.out.println(" ERRO: Ticket ou Authenticator ausente");
            response.setStatus(401);
            return false;
        }
        
        try {
            System.out.println(" Validando ticket com Kerberos...");
            String validationJson = kerberosClient.validateTicket(ticket, authenticator);
            System.out.println(" Resposta do Kerberos: " + validationJson);
            System.out.println("URL Kerberos: " + kerberosClient.toString());
            System.out.println("Validation JSON completo: " + validationJson);
            
            if (validationJson != null && validationJson.contains("\"valid\":true")) {
                System.out.println(" Autenticação válida!");

                String authenticatedEmail = extrairEmail(validationJson);
                if (authenticatedEmail != null && !authenticatedEmail.isBlank()) {
                    request.setAttribute(ATTR_AUTHENTICATED_EMAIL, authenticatedEmail);
                    log.info("[KERBEROS-AUTH] Identidade autenticada resolvida: email={} path={}", authenticatedEmail, path);
                } else {
                    log.warn("[KERBEROS-AUTH] Kerberos validou o ticket mas não devolveu email (resposta antiga?). path={}", path);
                }
                return true;
            }
            System.out.println(" Autenticação inválida!");
            response.setStatus(401);
            return false;
        } catch (Exception e) {
            System.out.println(" Erro ao validar: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(401);
            return false;
        }
    }
    
    private String extrairEmail(String validationJson) {
        try {
            JsonNode node = objectMapper.readTree(validationJson);
            JsonNode emailNode = node.get("email");
            if (emailNode == null || emailNode.isNull()) {
                return null;
            }
            String email = emailNode.asText();
            return (email == null || email.isBlank()) ? null : email;
        } catch (Exception e) {
            log.warn("[KERBEROS-AUTH] Falha ao extrair email da resposta de validação: {}", e.getMessage());
            return null;
        }
    }

    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/infraestruturas/listar-todas") || 
               path.startsWith( "/api/admin/infraestruturas/nao-registadas") || 
               path.startsWith("/api/infraestruturas/estatisticas") ||
               path.startsWith("/api/admin/dashboard") ||
               path.startsWith("/api/admin/infraestruturas") ||
               path.startsWith("/api/admin/utilizadores") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator");
    }
}