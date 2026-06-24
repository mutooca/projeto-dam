package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AnuncioDao;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnuncioRepository {
    private static final String TAG = "AnuncioRepository";
    private final AnuncioDao anuncioDao;
    private final AppExecutors executors;
    private final MutableLiveData<List<Anuncio>> anunciosLiveData = new MutableLiveData<>();
    private long lastFetchTime = 0;
    private static final long FETCH_THRESHOLD = 60000;

    public AnuncioRepository(Context context) {
        this.anuncioDao = AppDatabase.getInstance(context).anuncioDao();
        this.executors = AppExecutors.getInstance();
    }

    public LiveData<List<Anuncio>> getMeusAnuncios(String email) {
        if (System.currentTimeMillis() - lastFetchTime > FETCH_THRESHOLD) {
            refreshMeusAnuncios(email);
        } else {
            loadFromCache();
        }
        return anunciosLiveData;
    }

    private void loadFromCache() {
        executors.diskIO().execute(() -> {
            List<Anuncio> cached = anuncioDao.getAll();
            anunciosLiveData.postValue(cached);
        });
    }

    public void refreshMeusAnuncios(String email) {
        loadFromCache();

        RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastFetchTime = System.currentTimeMillis();
                    executors.diskIO().execute(() -> {
                        anuncioDao.deleteSyncedAds(); // Mantém os locais para testes
                        for(Anuncio a : response.body()) a.setUsuarioEmail(email);
                        anuncioDao.insertAll(response.body());
                        anunciosLiveData.postValue(anuncioDao.getAll());
                    });
                }
            }
            @Override public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                Log.e(TAG, "Falha ao sincronizar anúncios", t);
            }
        });
    }

    public void postarAnuncio(Map<String, Object> request, RepoCallback<Anuncio> callback) {
        RetrofitClient.getInstance().getApi().postarAnuncio(request).enqueue(new Callback<Anuncio>() {
            @Override
            public void onResponse(Call<Anuncio> call, Response<Anuncio> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lastFetchTime = 0;
                    executors.diskIO().execute(() -> anuncioDao.insertAll(List.of(response.body())));
                    callback.onSuccess(response.body());
                } else {
                    Log.w(TAG, "Falha no servidor (Erro " + response.code() + "). Salvando localmente...");
                    
                    Anuncio fallbackAd = new Anuncio();
                    fallbackAd.setTitulo((String) request.get("titulo"));
                    fallbackAd.setConteudo((String) request.get("conteudo"));
                    fallbackAd.setCategoria((String) request.get("tipo_politica"));
                    fallbackAd.setModo_entrega((String) request.get("modo_entrega"));
                    fallbackAd.setRestricaoPerfil((String) request.get("restricoes_perfil"));
                    fallbackAd.setAutorEmail((String) request.get("emailAutor"));
                    fallbackAd.setUsuarioEmail((String) request.get("emailAutor"));
                    fallbackAd.setDataPublicacao(LocalDateTime.now());
                    fallbackAd.setEstado("PENDENTE_LOCAL");
                    
                    executors.diskIO().execute(() -> {
                        anuncioDao.insertAll(List.of(fallbackAd));
                        loadFromCache();
                    });
                    callback.onSuccess(fallbackAd);
                }
            }

            @Override 
            public void onFailure(Call<Anuncio> call, Throwable t) { 
                Log.e(TAG, "Erro de rede. Salvando localmente...", t);
                
                Anuncio fallbackAd = new Anuncio();
                fallbackAd.setTitulo((String) request.get("titulo"));
                fallbackAd.setConteudo((String) request.get("conteudo"));
                fallbackAd.setCategoria((String) request.get("tipo_politica"));
                fallbackAd.setModo_entrega((String) request.get("modo_entrega"));
                fallbackAd.setRestricaoPerfil((String) request.get("restricoes_perfil"));
                fallbackAd.setAutorEmail((String) request.get("emailAutor"));
                fallbackAd.setUsuarioEmail((String) request.get("emailAutor"));
                fallbackAd.setDataPublicacao(LocalDateTime.now());
                fallbackAd.setEstado("ERRO_REDE_LOCAL");

                executors.diskIO().execute(() -> {
                    anuncioDao.insertAll(List.of(fallbackAd));
                    loadFromCache();
                });
                callback.onSuccess(fallbackAd);
            }
        });
    }

    public void removerAnuncio(UUID id, String email, RepoCallback<Void> callback) {
        RetrofitClient.getInstance().getApi().removerAnuncio(id.toString(), email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                executors.diskIO().execute(() -> {
                    anuncioDao.deleteById(id);
                    loadFromCache();
                });
                callback.onSuccess(null);
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) { 
                executors.diskIO().execute(() -> {
                    anuncioDao.deleteById(id);
                    loadFromCache();
                });
                callback.onSuccess(null);
            }
        });
    }

    public void listarPorLocal(UUID localId, RepoCallback<List<Anuncio>> callback) {
        RetrofitClient.getInstance().getApi().listarPorLocal(localId.toString()).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Erro local");
            }
            @Override public void onFailure(Call<List<Anuncio>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
