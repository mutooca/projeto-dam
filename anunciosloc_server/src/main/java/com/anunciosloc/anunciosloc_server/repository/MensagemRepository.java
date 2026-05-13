package com.anunciosloc.anunciosloc_server.repository;



import com.anunciosloc.anunciosloc_server.model.Mensagem;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    // Busca mensagens de um local que o utilizador AINDA NÃO visualizou
    @Query("SELECT m FROM Mensagem m WHERE m.local = :local AND :user NOT MEMBER OF m.visualizadaPor")
    List<Mensagem> findByLocalAndUserNotVisualized(@Param("local") Infraestrutura local, @Param("user") Utilizador user);


    List<Mensagem> findByLocalAndEntregueFalse(Infraestrutura local);
    List<Mensagem> findByLocal(Infraestrutura local);
    List<Mensagem> findByAutor(Utilizador autor);

}
