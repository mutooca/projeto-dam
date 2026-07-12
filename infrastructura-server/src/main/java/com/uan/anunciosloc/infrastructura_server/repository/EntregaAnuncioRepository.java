package com.uan.anunciosloc.infrastructura_server.repository;

import com.uan.anunciosloc.infrastructura_server.model.EntregaAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntregaAnuncioRepository extends JpaRepository<EntregaAnuncio, UUID> {

   
    Optional<EntregaAnuncio> findByIdAnuncioAndEmailUtilizador(UUID idAnuncio, String emailUtilizador);

    boolean existsByIdAnuncioAndEmailUtilizador(UUID idAnuncio, String emailUtilizador);

    List<EntregaAnuncio> findByIdAnuncio(UUID idAnuncio);

    
    List<EntregaAnuncio> findByEmailUtilizador(String emailUtilizador);

    
    @Query("SELECT COUNT(e) FROM EntregaAnuncio e WHERE e.idAnuncio = :idAnuncio")
    long countEntregasByAnuncio(@Param("idAnuncio") UUID idAnuncio);

    
    @Query("SELECT COUNT(e) FROM EntregaAnuncio e WHERE e.idAnuncio = :idAnuncio AND e.estadoEntrega = 'LIDO'")
    long countLeiturasByAnuncio(@Param("idAnuncio") UUID idAnuncio);

    
    @Query("SELECT e FROM EntregaAnuncio e " +
           "WHERE e.emailUtilizador = :email " +
           "AND e.estadoEntrega = 'ENTREGUE' " +
           "ORDER BY e.dataEntrega DESC")
    List<EntregaAnuncio> findEntregasNaoLidasByEmail(@Param("email") String email);

    @Query("SELECT e FROM EntregaAnuncio e " +
           "WHERE e.emailUtilizador = :email " +
           "AND e.estadoEntrega = 'LIDO' " +
           "ORDER BY e.dataLeitura DESC")
    List<EntregaAnuncio> findEntregasLidasByEmail(@Param("email") String email);


    @Query("SELECT e FROM EntregaAnuncio e " +
           "WHERE e.idAnuncio = :idAnuncio " +
           "AND e.estadoEntrega = 'ENTREGUE'")
    List<EntregaAnuncio> findEntregasNaoLidasByAnuncio(@Param("idAnuncio") UUID idAnuncio);

    @Query("SELECT e FROM EntregaAnuncio e " +
           "WHERE e.idAnuncio = :idAnuncio " +
           "AND e.estadoEntrega = 'LIDO'")
    List<EntregaAnuncio> findEntregasLidasByAnuncio(@Param("idAnuncio") UUID idAnuncio);

    
    List<EntregaAnuncio> findByModo(String modo);

    @Query("SELECT e FROM EntregaAnuncio e " +
           "WHERE e.emailUtilizador = :email " +
           "AND e.idInfraestrutura = :idInfra " +
           "AND e.estadoEntrega = 'LIDO'")
    List<EntregaAnuncio> findLeiturasByEmailAndInfra(
            @Param("email") String email,
            @Param("idInfra") UUID idInfra);
}