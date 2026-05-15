package com.anunciosloc.anunciosloc_server.security;

import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class KerberosAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private KerberosSoapClient kerberosClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
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
            
            if (validationJson != null && validationJson.contains("\"valid\":true")) {
                System.out.println(" Autenticação válida!");
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
    
    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator");
    }
}