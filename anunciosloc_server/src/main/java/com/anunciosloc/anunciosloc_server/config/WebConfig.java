package com.anunciosloc.anunciosloc_server.config;

import com.anunciosloc.anunciosloc_server.security.KerberosAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private KerberosAuthInterceptor kerberosAuthInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(kerberosAuthInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/registar", "/api/auth/login", "/kerberos/**");
    }
}