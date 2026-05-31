package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.CoordenadaGps;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CoordenadaGpsRepository extends JpaRepository<CoordenadaGps, UUID> {
}