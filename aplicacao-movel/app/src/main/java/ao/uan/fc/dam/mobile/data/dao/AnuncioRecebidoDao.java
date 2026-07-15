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

    @Query("SELECT * FROM anuncios_recebidos ORDER BY data_rececao DESC")
    List<AnuncioRecebido> listarTodos();

    @Query("SELECT * FROM anuncios_recebidos ORDER BY data_rececao DESC")
    LiveData<List<AnuncioRecebido>> listarTodosLiveData();

    @Query("SELECT * FROM anuncios_recebidos WHERE msg_id = :msgId LIMIT 1")
    AnuncioRecebido buscarPorMsgId(String msgId);

}
