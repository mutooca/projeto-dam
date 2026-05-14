package com.anunciosloc.anunciosloc_server.security;


import com.anunciosloc.anunciosloc_server.client.KerberosSoapClient;
import com.anunciosloc.anunciosloc_server.client.ValidationResponse;
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
        
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        String path = request.getRequestURI();
        
        // Rotas públicas
        if (isPublicRoute(path)) {
            return true;
        }
        
        String ticket = request.getHeader("X-Kerberos-Ticket");
        String authenticator = request.getHeader("X-Kerberos-Authenticator");
        
        if (ticket == null || authenticator == null) {
            response.setStatus(401);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\": \"Missing Kerberos credentials\"}");
            } catch (Exception e) {}
            return false;
        }
        
        try {
            ValidationResponse validation = kerberosClient.validateTicket(ticket, authenticator);
            if (!validation.isValid()) {
                response.setStatus(401);
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"error\": \"" + validation.getMessage() + "\"}");
                } catch (Exception e) {}
                return false;
            }
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
    
    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs");
    }
}