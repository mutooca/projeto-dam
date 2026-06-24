package ao.uan.fc.dam.mobile.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import java.util.UUID;
import ao.uan.fc.dam.mobile.model.Local;

@Dao
public interface LocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Local> locais);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Local local);

    @Query("SELECT * FROM locais ORDER BY nome ASC")
    LiveData<List<Local>> getAll();

    @Query("DELETE FROM locais WHERE idLocal = :id")
    void deleteById(UUID id);

    @Query("DELETE FROM locais")
    void deleteAll();
}
