package ao.uan.fc.dam.mobile.data.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseProvider {
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "anuncioloc.db")
                            .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}