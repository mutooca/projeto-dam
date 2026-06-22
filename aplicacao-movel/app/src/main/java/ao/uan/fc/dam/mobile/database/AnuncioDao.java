package ao.uan.fc.dam.mobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import ao.uan.fc.dam.mobile.model.Anuncio;

@Dao
public interface AnuncioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Anuncio> anuncios);

    @Query("SELECT * FROM anuncios ORDER BY dataPublicacao DESC")
    List<Anuncio> getAll();

    @Query("SELECT * FROM anuncios WHERE autorEmail = :email AND modo_entrega = 'DESCENTRALIZADO'")
    List<Anuncio> getMyDecentralizedAds(String email);

    @Query("DELETE FROM anuncios")
    void deleteAll();
}
