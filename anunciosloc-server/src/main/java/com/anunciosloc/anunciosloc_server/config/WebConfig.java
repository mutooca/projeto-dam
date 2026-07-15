package com.anunciosloc.anunciosloc_server.config;

import com.anunciosloc.anunciosloc_server.security.KerberosAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final KerberosAuthInterceptor kerberosAuthInterceptor;

    WebConfig(KerberosAuthInterceptor kerberosAuthInterceptor) {
        this.kerberosAuthInterceptor = kerberosAuthInterceptor;
    }
    
    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(kerberosAuthInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/registar", 
                "/api/auth/login",
                "/api/auth/login/admin",
                "/api/infraestruturas/locais/todos",
                "/api/infraestruturas/estatisticas",
                 "/api/admin/me",
                "/api/admin/dashboard",
                "/api/admin/infraestruturas",
                "/api/admin/infraestruturas/nao-registadas",
                "/api/admin/utilizadores",
                "/api/admin/utilizadores/inativos",
                "/api/admin/utilizadores/ativos",
                "/api/admin/utilizadores/contagem",
                "/h2-console/**",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            );
    }

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // ← PERMITE QUALQUER ORIGEM COM CREDENCIAIS
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)      // ← PERMITE HEADERS DE AUTENTICAÇÃO
                .maxAge(3600);
    }
}