package com.anunciosloc.anunciosloc_server.config;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final InfraestruturaRepository infraRepository;
    private final UtilizadorRepository utilizadorRepository;
    
    @Override
    public void run(String... args) {
        
        if (!utilizadorRepository.existsByEmail("admin@anunciosloc.com")) {
            Utilizador admin = new Utilizador();
            admin.setEmail("admin@anunciosloc.com");
            admin.setPasswordHash("admin123");
            admin.setSaldo(1000);
            admin.setRole("ADMIN");
            admin.setDataRegisto(LocalDateTime.now());
            admin.setAtivo(true);
            utilizadorRepository.save(admin);
            System.out.println("Admin criado: admin@anunciosloc.com / admin123");
        }
        
    
        if (!utilizadorRepository.existsByEmail("joao@teste.com")) {
            Utilizador user = new Utilizador();
            user.setEmail("joao@teste.com");
            user.setPasswordHash("123456");
            user.setSaldo(10);
            user.setRole("USER");
            user.setDataRegisto(LocalDateTime.now());
            user.setAtivo(true);
            utilizadorRepository.save(user);
            System.out.println("Utilizador criado: joao@teste.com / 123456");
        }
        
        
        if (infraRepository.count() == 0) {
            infraRepository.save(new Infraestrutura(null, "Largo da Independência", -8.8383, 13.2344, 100, 25, 0, 0));
            infraRepository.save(new Infraestrutura(null, "Belas Shopping", -8.9519, 13.1708, 200, 50, 0, 0));
            infraRepository.save(new Infraestrutura(null, "Ginásio do Camama I", -8.9088, 13.2048, 50, 10, 0, 0));
            infraRepository.save(new Infraestrutura(null, "Marginal", -8.8000, 13.2333, 300, 80, 0, 0));
            System.out.println("4 infraestruturas criadas");
        }
    }
}