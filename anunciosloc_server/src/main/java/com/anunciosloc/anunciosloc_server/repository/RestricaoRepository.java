package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Restricao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestricaoRepository extends JpaRepository<Restricao, Long> {
    List<Restricao> findByInfraestrutura(Infraestrutura infra);
}