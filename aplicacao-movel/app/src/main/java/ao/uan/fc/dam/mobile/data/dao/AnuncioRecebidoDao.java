package ao.uan.fc.dam.mobile.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;

@Dao
public interface AnuncioRecebidoDao {

    @Insert
    long inserir(AnuncioRecebido anuncio);

    @Query("DELETE FROM anuncios_recebidos WHERE id_utilizador = :idUtilizador")
    void deletarTodosDoUtilizador(int idUtilizador);

    @Query("SELECT * FROM anuncios_recebidos WHERE id_utilizador = :idUtilizador ORDER BY data_rececao DESC")
    List<AnuncioRecebido> listarTodos(int idUtilizador);

    @Query("SELECT * FROM anuncios_recebidos WHERE id_utilizador = :idUtilizador ORDER BY data_rececao DESC")
    LiveData<List<AnuncioRecebido>> listarTodosLiveData(int idUtilizador);

    @Query("SELECT * FROM anuncios_recebidos WHERE msg_id = :msgId LIMIT 1")
    AnuncioRecebido buscarPorMsgId(String msgId);

    @Query("SELECT * FROM anuncios_recebidos WHERE msg_id = :msgId AND id_utilizador = :idUtilizador LIMIT 1")
    AnuncioRecebido buscarPorMsgId(String msgId, int idUtilizador);

}
