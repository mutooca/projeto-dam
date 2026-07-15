package ao.uan.fc.dam.mobile.data.dao;

import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.Historico;

@Dao
public interface HistoricoDao {

    @Insert
    long inserir(Historico historico);

    @Update
    void atualizar(Historico historico);

    @Delete
    void remover(Historico historico);

    @Query(" SELECT * FROM historico ORDER BY registo DESC")
    List<Historico> listarTodos();

    @Query(" SELECT * FROM historico WHERE id_utilizador=:id ORDER BY registo DESC")
    List<Historico> listarPorUtilizador(int id);
}