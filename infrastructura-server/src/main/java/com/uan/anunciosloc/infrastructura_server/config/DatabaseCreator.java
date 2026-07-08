package com.uan.anunciosloc.infrastructura_server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.*;

@Slf4j
@Component
public class DatabaseCreator {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:postgres}")
    private String username;

    @Value("${spring.datasource.password:kama2}")
    private String password;

    // Este método corre depois do contexto estar pronto
    // mas só serve para confirmar — a BD já foi criada
    // pelo método estático abaixo
    @EventListener(ContextRefreshedEvent.class)
    public void confirmar() {
        String dbName = extrairNomeBd(datasourceUrl);
        log.info("Infraestrutura a usar BD: '{}'", dbName);
    }

    // Método estático chamado pelo main ANTES do Spring arrancar
    public static void criarSeNaoExistir(String url, String user, String pass) {
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        String adminUrl = url.substring(0, url.lastIndexOf("/")) + "/postgres";

        System.out.println("[DB] A verificar BD '" + dbName + "'...");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver não encontrado: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(adminUrl, user, pass)) {
            ResultSet rs = conn.getMetaData().getCatalogs();
            boolean existe = false;
            while (rs.next()) {
                if (dbName.equalsIgnoreCase(rs.getString(1))) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                System.out.println("[DB] BD '" + dbName + "' não existe — a criar...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE DATABASE \"" + dbName + "\"");
                    System.out.println("[DB] BD '" + dbName + "' criada com sucesso");
                }
            } else {
                System.out.println("[DB] BD '" + dbName + "' já existe");
            }

        } catch (Exception e) {
            System.err.println("[DB] Erro: " + e.getMessage());
            throw new RuntimeException("Não foi possível inicializar a BD", e);
        }
    }

    private String extrairNomeBd(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}