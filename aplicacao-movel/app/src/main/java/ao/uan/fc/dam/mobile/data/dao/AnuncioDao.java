package ao.uan.fc.dam.mobile.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;

@Dao
public interface AnuncioDao {

    @Insert
    long inserir(Anuncio anuncio);

    @Update
    void atualizar(Anuncio anuncio);

    @Delete
    void remover(Anuncio anuncio);

    @Query("SELECT * FROM anuncios")
    List<Anuncio> listarTodos();

    @Query("SELECT * FROM anuncios WHERE id_anuncio = :id")
    Anuncio buscarPorId(int id);

    @Query("SELECT * FROM anuncios WHERE id_local = :idLocal")
    List<AnuncioCompleto> listarPorLocal(int idLocal);

    @Query("SELECT * FROM anuncios WHERE id_utilizador = :idUtilizador")
    List<Anuncio> listarPorUtilizador(int idUtilizador);

    @Transaction
    @Query("SELECT * FROM anuncios ORDER BY data_publicacao DESC")
    List<AnuncioCompleto> listarTodosComRelacionamentos();

    @Transaction
    @Query("SELECT * FROM anuncios WHERE id_utilizador = :idUtilizador ORDER BY data_publicacao DESC")
    LiveData<List<AnuncioCompleto>> listarTodosComRelacionamentosLiveData(int idUtilizador);

    @Transaction
    @Query("SELECT * FROM anuncios WHERE id_anuncio=:id")
    AnuncioCompleto buscarCompleto(int id);

    @Query("SELECT COUNT(*) FROM anuncios WHERE id_utilizador = :idUtilizador")
    int contarPorUtilizador(int idUtilizador);

    @Query("SELECT * FROM anuncios WHERE id_utilizador = :idUtilizador ORDER BY data_publicacao DESC")
    List<Anuncio> listarAnunciosDoUtilizador(int idUtilizador);
}