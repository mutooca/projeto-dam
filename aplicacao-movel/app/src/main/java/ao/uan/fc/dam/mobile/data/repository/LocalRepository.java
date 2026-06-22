package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private final LocalDao localDao;
    private final AppExecutors executors;
    private final MutableLiveData<List<Local>> localesLiveData = new MutableLiveData<>();
    private long lastFetchTime = 0;
    private static final long FETCH_THRESHOLD = 300000; // 5 minutos em milissegundos

    public LocalRepository(Context context) {
        this.localDao = AppDatabase.getInstance(context).localDao();
        this.executors = AppExecutors.getInstance();
    }

    public LiveData<List<Local>> getLocales() {
        if (System.currentTimeMillis() - lastFetchTime > FETCH_THRESHOLD) {
            refreshLocales();
        } else {
            loadFromCache();
        }
        return localesLiveData;
    }

    public void loadFromCache() {
        executors.diskIO().execute(() -> {
            List<Local> cached = localDao.getAll();
            localesLiveData.postValue(cached);
        });
    }

    public void refreshLocales() {
        loadFromCache();

        RetrofitClient.getInstance().getApi().listarLocais(0.0, 0.0).enqueue(new Callback<List<Local>>() {
            @Override
            public void onResponse(Call<List<Local>> call, Response<List<Local>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastFetchTime = System.currentTimeMillis();
                    executors.diskIO().execute(() -> {
                        localDao.deleteAll();
                        localDao.insertAll(response.body());
                        localesLiveData.postValue(response.body());
                    });
                }
            }
            @Override public void onFailure(Call<List<Local>> call, Throwable t) {}
        });
    }

    public void createLocal(Map<String, Object> request, RepoCallback<Local> callback) {
        RetrofitClient.getInstance().getApi().criarLocal(request).enqueue(new Callback<Local>() {
            @Override
            public void onResponse(Call<Local> call, Response<Local> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> localDao.insert(response.body()));
                    lastFetchTime = 0; // Força refresh na próxima vez
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro na criação");
                }
            }
            @Override public void onFailure(Call<Local> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void deleteLocal(UUID id, String email, RepoCallback<Void> callback) {
        RetrofitClient.getInstance().getApi().removerLocal(id.toString(), email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executors.diskIO().execute(() -> localDao.deleteById(id));
                    lastFetchTime = 0;
                    callback.onSuccess(null);
                } else {
                    callback.onError("Erro na remoção");
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
