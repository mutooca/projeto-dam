/*package com.anunciosloc.anunciosloc_server.config;


import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    
    private final UtilizadorRepository utilizadorRepository;
    
    @Override
public void run(String... args) {

    if (!utilizadorRepository.existsByEmail("admin@anunciosloc.com")) {
        Utilizador admin = new Utilizador();
        admin.setNome("Administrador");       
        admin.setEmail("admin@anunciosloc.com");
        admin.setPalavraChave("admin123");
        admin.setSaldo(1000);
        admin.setRole("ADMIN");
        admin.setDataCriacao(LocalDateTime.now());
        admin.setAtivo(true);
        utilizadorRepository.save(admin);
        System.out.println("Admin criado: admin@anunciosloc.com / admin123");
    }

    if (!utilizadorRepository.existsByEmail("joao@teste.com")) {
        Utilizador user = new Utilizador();
        user.setNome("João Teste");     
        user.setEmail("joao@teste.com");
        user.setPalavraChave("123456");
        user.setSaldo(10);
        user.setRole("USER");
        user.setDataCriacao(LocalDateTime.now());
        user.setAtivo(true);
        utilizadorRepository.save(user);
        System.out.println("Utilizador criado: joao@teste.com / 123456");
    }

    if (!utilizadorRepository.existsByEmail("kama@teste.com")) {
        Utilizador user = new Utilizador();
        user.setNome("Kama Teste");     
        user.setEmail("kama@teste.com");
        user.setPalavraChave("1234567");
        user.setSaldo(10);
        user.setRole("USER");
        user.setDataCriacao(LocalDateTime.now());
        user.setAtivo(true);
        utilizadorRepository.save(user);
        System.out.println("Utilizador criado: kama@teste.com / 1234567");
    }
}
}*/