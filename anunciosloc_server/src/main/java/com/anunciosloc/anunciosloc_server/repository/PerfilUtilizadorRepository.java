package com.anunciosloc.anunciosloc_server.repository;

import com.anunciosloc.anunciosloc_server.model.PerfilUtilizador;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface PerfilUtilizadorRepository
        extends JpaRepository<PerfilUtilizador, UUID> {

    
    List<PerfilUtilizador> findByUtilizador(Utilizador utilizador);

   
    @Query("SELECT DISTINCT p.chave FROM PerfilUtilizador p ORDER BY p.chave")
    List<String> findTodasAsChaves();

    
    boolean existsByUtilizadorAndChaveAndValor(Utilizador utilizador,
                                                String chave,
                                                String valor);

    void deleteByUtilizadorAndChave(Utilizador utilizador, String chave);
}