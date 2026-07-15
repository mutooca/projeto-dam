package ao.uan.fc.dam.mobile.data.dao;

import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;

@Dao
public interface CoordenadaGpsDao {

    @Insert
    long inserir(CoordenadaGps gps);

    @Update
    void atualizar(CoordenadaGps gps);

    @Delete
    void remover(CoordenadaGps gps);

    @Query("SELECT * FROM coordenada_gps")
    List<CoordenadaGps> listarTodos();

    @Query("SELECT * FROM coordenada_gps WHERE id_local=:id")
    CoordenadaGps buscarPorLocal(int id);

    @Query("DELETE FROM coordenada_gps WHERE id_local = :idLocal")
    void removerPorLocal(int idLocal);
}