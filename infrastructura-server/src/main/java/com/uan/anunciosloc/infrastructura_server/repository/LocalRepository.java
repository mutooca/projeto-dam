package com.uan.anunciosloc.infrastructura_server.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.uan.anunciosloc.infrastructura_server.model.Local;

public interface LocalRepository
        extends JpaRepository<Local, UUID> {
    Optional<Local> findByNome(String nome);
    @NonNull
    List<Local> findAll();
    boolean existsByNomeIgnoreCase(String nome);
}