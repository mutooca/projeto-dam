package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.Anuncio;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Local;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

    List<Anuncio> findByLocal(Local local);
    List<Anuncio> findByLocalId(UUID localId);
    List<Anuncio> findByAutor(Utilizador autor);
    List<Anuncio> findByInfraestrutura(Infraestrutura infra);
    List<Anuncio> findByInfraestruturaAndEstado(Infraestrutura infra, String estado);
    List<Anuncio> findByLocalAndEstadoAndCategoriaIn(Local local, String estado, List<String> categorias);
    
    
    @Query("SELECT a FROM Anuncio a WHERE a.local.idLocal = :localId AND a.idAnuncio NOT IN " +
           "(SELECT ea.anuncio.idAnuncio FROM EntregaAnuncio ea WHERE ea.utilizador.email = :email)")
    List<Anuncio> findNaoVisualizadosPorUtilizador(@Param("localId") UUID localId, @Param("email") String email);
    List<Anuncio> findByInfraestruturaAndEstadoAndCategoriaIn(Infraestrutura infra, String string,
            List<String> categorias);
}