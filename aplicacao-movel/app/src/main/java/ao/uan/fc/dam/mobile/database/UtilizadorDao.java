package ao.uan.fc.dam.mobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ao.uan.fc.dam.mobile.model.Utilizador;

@Dao
public interface UtilizadorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Utilizador utilizador);

    @Query("SELECT * FROM utilizadores LIMIT 1")
    Utilizador getProfile();

    @Query("UPDATE utilizadores SET nome = :nome")
    void updateName(String nome);

    @Query("UPDATE utilizadores SET preferenciaAnuncio = :prefs")
    void updatePreferences(String prefs);

    @Query("DELETE FROM utilizadores")
    void deleteProfile();
}
