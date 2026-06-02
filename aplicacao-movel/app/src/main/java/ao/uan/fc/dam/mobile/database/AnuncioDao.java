package ao.uan.fc.dam.mobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ao.uan.fc.dam.mobile.model.Anuncio;
import java.util.List;

@Dao
public interface AnuncioDao {
    @Query("DELETE FROM anuncios WHERE usuarioEmail = :email AND categoria = :categoria")
    void limparPorCategoria(String email, String categoria);

    @Query("SELECT * FROM anuncios WHERE usuarioEmail = :email AND categoria = :categoria")
    List<Anuncio> listarPorCategoria(String email, String categoria);

    @Query("DELETE FROM anuncios WHERE id = :id")
    void removerPorId(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void salvarTodos(List<Anuncio> anuncios);
}
