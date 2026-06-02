package ao.uan.fc.dam.mobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ao.uan.fc.dam.mobile.model.Local;
import java.util.List;

@Dao
public interface LocalDao {
    @Query("DELETE FROM locais")
    void limparTudo();

    @Query("SELECT * FROM locais")
    List<Local> listarTodos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void salvar(Local local);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void salvarTodos(List<Local> locais);
}
