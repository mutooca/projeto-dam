package com.uan.anunciosloc.infrastructura_server.repository;

import com.uan.anunciosloc.infrastructura_server.model.CoordenadaGps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoordenadaGpsRepository extends JpaRepository<CoordenadaGps, UUID> {
}