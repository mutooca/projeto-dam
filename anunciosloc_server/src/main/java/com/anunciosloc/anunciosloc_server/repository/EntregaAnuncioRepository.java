package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Anuncio;
import com.anunciosloc.anunciosloc_server.model.EntregaAnuncio;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EntregaAnuncioRepository extends JpaRepository<EntregaAnuncio, UUID> {
    boolean existsByAnuncioAndUtilizador(Anuncio anuncio, Utilizador utilizador);
    List<EntregaAnuncio> findByUtilizador(Utilizador utilizador);
    List<EntregaAnuncio> findByInfraestrutura(Infraestrutura infra);
    long countByAnuncio(Anuncio anuncio);
}