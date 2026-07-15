package ao.uan.fc.dam.mobile.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ao.uan.fc.dam.mobile.data.converter.EnumConverter;
import ao.uan.fc.dam.mobile.data.converter.LocalDateTimeConverter;
import ao.uan.fc.dam.mobile.data.dao.AnuncioDao;
import ao.uan.fc.dam.mobile.data.dao.AnuncioRecebidoDao;
import ao.uan.fc.dam.mobile.data.dao.AtributoPerfilDao;
import ao.uan.fc.dam.mobile.data.dao.CoordenadaGpsDao;
import ao.uan.fc.dam.mobile.data.dao.CoordenadaWifiDao;
import ao.uan.fc.dam.mobile.data.dao.HistoricoDao;
import ao.uan.fc.dam.mobile.data.dao.LocalDao;
import ao.uan.fc.dam.mobile.data.dao.UtilizadorDao;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.entity.Historico;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;

@Database(
        entities = {
                Utilizador.class,
                Local.class,
                CoordenadaGps.class,
                CoordenadaWifi.class,
                Anuncio.class,
                Historico.class,
                AtributoPerfil.class,
                AnuncioRecebido.class
        },
        version = 6,
        exportSchema = false
)
@TypeConverters({
        LocalDateTimeConverter.class,
        EnumConverter.class
})

public abstract class AppDatabase extends RoomDatabase {
    public abstract UtilizadorDao utilizadorDao();
    public abstract LocalDao localDao();
    public abstract CoordenadaGpsDao coordenadaGpsDao();
    public abstract CoordenadaWifiDao coordenadaWifiDao();
    public abstract AnuncioDao anuncioDao();
    public abstract HistoricoDao historicoDao();
    public abstract AtributoPerfilDao atributoPerfilDao();
    public abstract AnuncioRecebidoDao anuncioRecebidoDao();
}
