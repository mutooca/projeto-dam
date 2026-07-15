package com.uan.anunciosloc.infrastructura_server;

import com.uan.anunciosloc.infrastructura_server.config.DatabaseCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfrastructuraServerApplication {

    public static void main(String[] args) {

        
        String url = "jdbc:postgresql://localhost:5432/bd_infra1"; 
        String user = "postgres";
        String pass = "kama2";

        for (String arg : args) {
            if (arg.startsWith("--spring.datasource.url=")) {
                url = arg.split("=", 2)[1];
            }
            if (arg.startsWith("--spring.datasource.username=")) {
                user = arg.split("=", 2)[1];
            }
            if (arg.startsWith("--spring.datasource.password=")) {
                pass = arg.split("=", 2)[1];
            }
            if (arg.startsWith("--DB_USER=")) {
                user = arg.split("=", 2)[1];
            }
            if (arg.startsWith("--DB_PASS=")) {
                pass = arg.split("=", 2)[1];
            }
        }

        // Cria a BD ANTES do Spring arrancar
        DatabaseCreator.criarSeNaoExistir(url, user, pass);

        SpringApplication.run(InfrastructuraServerApplication.class, args);
    }
}