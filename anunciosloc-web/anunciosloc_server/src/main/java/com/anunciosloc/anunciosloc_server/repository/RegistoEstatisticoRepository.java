package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.RegistoEstatistico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistoEstatisticoRepository extends JpaRepository<RegistoEstatistico, Long> {
    Optional<RegistoEstatistico> findByInfraestruturaAndDataRegisto(Infraestrutura infra, LocalDate data);
    List<RegistoEstatistico> findByInfraestrutura(Infraestrutura infra);
}