package ao.uan.fc.dam.mobile.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.Utilizador;

@Database(entities = {Anuncio.class, Local.class, Utilizador.class}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract AnuncioDao anuncioDao();
    public abstract LocalDao localDao();
    public abstract UtilizadorDao utilizadorDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "anunciosloc_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
