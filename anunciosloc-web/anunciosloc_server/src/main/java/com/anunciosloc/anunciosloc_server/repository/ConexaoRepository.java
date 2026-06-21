package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Conexao;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConexaoRepository extends JpaRepository<Conexao, Long> {
    Optional<Conexao> findByUtilizadorAndInfraestrutura(Utilizador utilizador, Infraestrutura infra);
    List<Conexao> findByInfraestrutura(Infraestrutura infra);
    long countByInfraestrutura(Infraestrutura infra);
    List<Conexao> findByDataConexaoAfter(LocalDateTime data);
}