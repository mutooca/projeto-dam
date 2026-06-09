package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Local;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LocalRepository extends JpaRepository<Local, UUID> {
    List<Local> findByInfraestrutura(Infraestrutura infra);

    
    List<Local> findByInfraestruturaIdInfraestrutura(UUID idInfraestrutura);
}