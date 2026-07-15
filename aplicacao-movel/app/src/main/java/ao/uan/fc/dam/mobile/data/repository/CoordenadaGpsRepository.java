package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;

import ao.uan.fc.dam.mobile.data.dao.CoordenadaGpsDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;


public class CoordenadaGpsRepository {
    private final CoordenadaGpsDao dao;

    public CoordenadaGpsRepository(Context context){
        dao =
                DatabaseProvider
                        .getInstance(context)
                        .coordenadaGpsDao();
    }

    public void inserir(
            CoordenadaGps gps,
            ResultadoCallback<Long> callback){
        DatabaseExecutor.executor.execute(() -> {
            long id = dao.inserir(gps);
            Log.d(
                    "GPS",
                    "Coordenada inserida ID="
                            + id
                            + " Local="
                            + gps.getIdLocal()
            );
            if(callback != null){
                callback.onResultado(id);
            }
        });
    }


    public void buscarPorLocal(
            int idLocal,
            ResultadoCallback<CoordenadaGps> callback
    ){
        DatabaseExecutor.executor.execute(() -> {
            CoordenadaGps gps =
                    dao.buscarPorLocal(idLocal);
            callback.onResultado(gps);
        });
    }
}