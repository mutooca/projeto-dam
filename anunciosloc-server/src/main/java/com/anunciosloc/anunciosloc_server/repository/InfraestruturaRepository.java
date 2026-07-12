package com.anunciosloc.anunciosloc_server.repository;



import com.anunciosloc.anunciosloc_server.model.Infraestrutura;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface InfraestruturaRepository extends JpaRepository<Infraestrutura, UUID> {

    
    Optional<Infraestrutura> findByNome(String nome);
    List<Infraestrutura> findByAtivaTrue();
    Optional<Infraestrutura> findByUrl(String url);
    
    // serve para impedir concorrencia
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Infraestrutura i WHERE i.id = :id")
    Optional<Infraestrutura> findByIdWithLock(@Param("id") UUID id);

    // buscar infra com coordenadas semelhantes
    @Query("SELECT i FROM Infraestrutura i WHERE " +
           "ABS(i.latitude - :latitude) < 0.01 AND " +
           "ABS(i.longitude - :longitude) < 0.01")
    List<Infraestrutura> findNearbyInfraestruturas(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude
    );
    
}
