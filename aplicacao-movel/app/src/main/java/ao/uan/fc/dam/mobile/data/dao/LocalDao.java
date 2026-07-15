package ao.uan.fc.dam.mobile.data.dao;

import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;

@Dao
public interface LocalDao {

    @Insert
    long inserir(Local local);

    @Update
    void atualizar(Local local);

    @Delete
    void remover(Local local);

    @Query("SELECT * FROM locais ORDER BY nome")
    List<Local> listarTodos();

    @Query("SELECT * FROM locais ORDER BY id_local DESC")
    List<Local> listarRecentes();

    @Query("SELECT * FROM locais WHERE id_local=:id")
    Local buscarPorId(int id);

    @Query("SELECT * FROM locais WHERE id_servidor = :idServidor ORDER BY id_local DESC LIMIT 1")
    Local buscarPorIdServidor(String idServidor);

    @Query("SELECT * FROM locais WHERE nome LIKE '%' || :nome || '%'")
    List<Local> pesquisar(String nome);

    @Transaction
    @Query("SELECT * FROM locais WHERE id_local=:id")
    LocalCompleto buscarCompleto(int id);

    @Transaction
    @Query("SELECT * FROM locais ORDER BY nome")
    List<LocalCompleto> listarTodosComCoordenadas();

    @Transaction
    @Query("SELECT * FROM locais WHERE nome LIKE '%' || :nome || '%'")
    List<LocalCompleto> pesquisarComCoordenadas(String nome);

    @Query("DELETE FROM locais")
    void limparTabela();
}
