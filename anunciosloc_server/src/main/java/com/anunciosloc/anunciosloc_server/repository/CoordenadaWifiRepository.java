package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.CoordenadaWifi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CoordenadaWifiRepository extends JpaRepository<CoordenadaWifi, UUID> {
}