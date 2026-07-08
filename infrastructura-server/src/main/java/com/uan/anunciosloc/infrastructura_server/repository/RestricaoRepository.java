package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.uan.anunciosloc.infrastructura_server.model.Restricao;

public interface RestricaoRepository
        extends JpaRepository<Restricao, UUID> {
    @NonNull
    List<Restricao> findAll();
}
