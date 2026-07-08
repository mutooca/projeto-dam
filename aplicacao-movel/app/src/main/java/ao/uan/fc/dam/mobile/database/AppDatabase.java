package ao.uan.fc.dam.mobile.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.Utilizador;

@Database(entities = {Anuncio.class, Local.class, Utilizador.class}, version = 8, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anuncios ADD COLUMN tipoPolitica TEXT");
            database.execSQL("ALTER TABLE anuncios ADD COLUMN nomeLocal TEXT");
        }
    };

    public abstract AnuncioDao anuncioDao();
    public abstract LocalDao localDao();
    public abstract UtilizadorDao utilizadorDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "anunciosloc_db")
                    .addMigrations(MIGRATION_7_8)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
