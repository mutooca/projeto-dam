package ao.uan.fc.dam.mobile.data.dao;

import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.Utilizador;

@Dao
public interface UtilizadorDao {

    @Insert
    long inserir(Utilizador utilizador);

    @Update
    void atualizar(Utilizador utilizador);

    @Delete
    void remover(Utilizador utilizador);

    @Query("SELECT * FROM utilizadores")
    List<Utilizador> listarTodos();

    @Query("SELECT * FROM utilizadores WHERE id_utilizador=:id")
    Utilizador buscarPorId(int id);

    @Query("SELECT * FROM utilizadores WHERE email=:email LIMIT 1")
    Utilizador buscarPorEmail(String email);

    @Query("DELETE FROM utilizadores")
    void limparTabela();

    @Query(" SELECT *FROM utilizadores WHERE email = :email AND palavra_chave = :palavraChave LIMIT 1")
    Utilizador autenticar(String email, String palavraChave);
}