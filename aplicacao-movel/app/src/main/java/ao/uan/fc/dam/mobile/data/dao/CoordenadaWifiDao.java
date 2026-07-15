package ao.uan.fc.dam.mobile.data.dao;

import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;

@Dao
public interface CoordenadaWifiDao {

    @Insert
    long inserir(CoordenadaWifi wifi);

    @Update
    void atualizar(CoordenadaWifi wifi);

    @Delete
    void remover(CoordenadaWifi wifi);

    @Query("SELECT * FROM coordenada_wifi")
    List<CoordenadaWifi> listarTodos();

    @Query("SELECT * FROM coordenada_wifi WHERE id_local=:id")
    CoordenadaWifi buscarPorLocal(int id);

}