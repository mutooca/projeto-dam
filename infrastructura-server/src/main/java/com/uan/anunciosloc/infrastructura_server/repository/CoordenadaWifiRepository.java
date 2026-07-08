package com.uan.anunciosloc.infrastructura_server.repository;

import com.uan.anunciosloc.infrastructura_server.model.CoordenadaWifi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoordenadaWifiRepository extends JpaRepository<CoordenadaWifi, UUID> {
}