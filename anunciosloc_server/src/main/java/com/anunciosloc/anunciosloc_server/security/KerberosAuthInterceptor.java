package com.anunciosloc.anunciosloc_server.security;

import com.anunciosloc.anunciosloc_server.kerberos.KerberosService;
import com.anunciosloc.anunciosloc_server.kerberos.KerberosTicket;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class KerberosAuthInterceptor implements HandlerInterceptor {
    
    private final KerberosService kerberosService;
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler) {
        
        // OPTIONS requests (CORS)
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        String path = request.getRequestURI();
        
        
        if (isPublicRoute(path)) {
            return true;
        }
        
        // Extrair ticket e autenticador dos headers
        String ticket = request.getHeader("X-Kerberos-Ticket");
        String authenticator = request.getHeader("X-Kerberos-Authenticator");
        
        if (ticket == null || authenticator == null) {
            response.setStatus(401);
            return false;
        }
        
        // Validar com Kerberos
        try {
            boolean isValid = kerberosService.validateTicketAndAuthenticator(ticket, authenticator);
            if (!isValid) {
                response.setStatus(401);
                return false;
            }
            
            // Extrair email do ticket para uso nas rotas
            KerberosTicket ticketObj = KerberosTicket.deserialize(ticket);
            request.setAttribute("authenticatedUser", ticketObj.getEmailUtilizador());
            
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
    
    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/kerberos/") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs");
    }
}