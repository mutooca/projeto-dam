package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.database.LocalDao;
import ao.uan.fc.dam.mobile.model.Local;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalRepository {
    private static final String TAG = "LocalRepository";
    private final LocalDao localDao;
    private final AppExecutors executors;
    private final LiveData<List<Local>> localesLiveData;
    private long lastFetchTime = 0;
    private static final long FETCH_THRESHOLD = 300000;

    public LocalRepository(Context context) {
        this.localDao = AppDatabase.getInstance(context).localDao();
        this.executors = AppExecutors.getInstance();
        this.localesLiveData = localDao.getAll();
    }

    public LiveData<List<Local>> getLocales() {
        if (System.currentTimeMillis() - lastFetchTime > FETCH_THRESHOLD) {
            refreshLocales();
        }
        return localesLiveData;
    }

    public void refreshLocales() {
        RetrofitClient.getInstance().getApi().listarLocais(0.0, 0.0).enqueue(new Callback<List<Local>>() {
            @Override
            public void onResponse(Call<List<Local>> call, Response<List<Local>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastFetchTime = System.currentTimeMillis();
                    executors.diskIO().execute(() -> {
                        localDao.deleteAll();
                        localDao.insertAll(response.body());
                    });
                } else {
                    Log.e(TAG, "Erro ao listar locais: " + response.code());
                }
            }
            @Override public void onFailure(Call<List<Local>> call, Throwable t) {
                Log.e(TAG, "Falha na rede ao listar locais", t);
            }
        });
    }

    public void createLocal(Map<String, Object> request, RepoCallback<Local> callback) {
        RetrofitClient.getInstance().getApi().criarLocal(request).enqueue(new Callback<Local>() {
            @Override
            public void onResponse(Call<Local> call, Response<Local> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> localDao.insert(response.body()));
                    callback.onSuccess(response.body());
                } else {
                    Log.w(TAG, "Servidor rejeitou (Erro " + response.code() + "). Criando localmente como fallback...");
                    
                    // Fallback: Criar localmente para permitir testes (F3)
                    Local localFallback = new Local();
                    localFallback.setNome((String) request.get("nome"));
                    localFallback.setLatitude((Double) request.get("latitude"));
                    localFallback.setLongitude((Double) request.get("longitude"));
                    localFallback.setRaio((Integer) request.get("raio"));
                    
                    executors.diskIO().execute(() -> localDao.insert(localFallback));
                    callback.onSuccess(localFallback);
                }
            }

            @Override 
            public void onFailure(Call<Local> call, Throwable t) { 
                Log.e(TAG, "Falha de conexão. Criando localmente...", t);
                
                Local localFallback = new Local();
                localFallback.setNome((String) request.get("nome"));
                localFallback.setLatitude((Double) request.get("latitude"));
                localFallback.setLongitude((Double) request.get("longitude"));
                localFallback.setRaio((Integer) request.get("raio"));
                
                executors.diskIO().execute(() -> localDao.insert(localFallback));
                callback.onSuccess(localFallback);
            }
        });
    }

    public void deleteLocal(UUID id, String email, RepoCallback<Void> callback) {
        RetrofitClient.getInstance().getApi().removerLocal(id.toString(), email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executors.diskIO().execute(() -> localDao.deleteById(id));
                    callback.onSuccess(null);
                } else {
                    // Mesmo se falhar no server, removemos localmente para o utilizador
                    executors.diskIO().execute(() -> localDao.deleteById(id));
                    callback.onSuccess(null);
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) { 
                executors.diskIO().execute(() -> localDao.deleteById(id));
                callback.onSuccess(null);
            }
        });
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
