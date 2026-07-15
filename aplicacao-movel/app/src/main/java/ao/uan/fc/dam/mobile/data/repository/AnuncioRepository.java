package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import ao.uan.fc.dam.mobile.data.dao.AnuncioDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnuncioRepository {
    private final AnuncioDao dao;
    private static final String TAG = "AnuncioRepository";

    public AnuncioRepository(Context context){
        dao = DatabaseProvider.getInstance(context).anuncioDao();
    }

    public void inserir(Anuncio anuncio, ResultadoCallback<Long> callback){
        // Primeiro insere localmente
        DatabaseExecutor.executor.execute(() ->{
            long id = dao.inserir(anuncio);
            anuncio.setIdAnuncio((int) id);
            
            // Se for modo CENTRALIZADO, envia para o servidor
            if (anuncio.getModoEntrega() != null && anuncio.getModoEntrega().name().equals("CENTRALIZADO")) {
                publicarNoServidor(anuncio);
            }

            if(callback != null){
                callback.onResultado(id);
            }
        });
    }

    private void publicarNoServidor(Anuncio anuncio) {
        RetrofitClient.getApiService().publicarAnuncio(anuncio).enqueue(new Callback<Anuncio>() {
            @Override
            public void onResponse(Call<Anuncio> call, Response<Anuncio> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Anúncio publicado no servidor com sucesso");
                } else {
                    Log.e(TAG, "Erro ao publicar no servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Anuncio> call, Throwable t) {
                Log.e(TAG, "Falha na rede ao publicar anúncio", t);
            }
        });
    }

    public void sincronizarAnunciosRemotos(ResultadoCallback<Void> callback) {
        RetrofitClient.getApiService().listarAnuncios().enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatabaseExecutor.executor.execute(() -> {
                        for (Anuncio anuncio : response.body()) {
                            dao.inserir(anuncio); // Room ignora se conflito se configurado ou atualiza
                        }
                        if (callback != null) callback.onResultado(null);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                Log.e(TAG, "Erro ao sincronizar anúncios", t);
                if (callback != null) callback.onResultado(null);
            }
        });
    }

    public void remover(Anuncio anuncio, ResultadoCallback<Void> callback){
        DatabaseExecutor.executor.execute(() -> {
            dao.remover(anuncio);
            if(callback != null){
                callback.onResultado(null);
            }
        });
    }

    public LiveData<List<AnuncioCompleto>> listarTodosLiveData() {
        return dao.listarTodosComRelacionamentosLiveData();
    }

    public void listarTodos(ResultadoCallback<List<AnuncioCompleto>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioCompleto> lista = dao.listarTodosComRelacionamentos();
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }

    public void buscarPorId(int id, ResultadoCallback<AnuncioCompleto> callback){
        DatabaseExecutor.executor.execute(() -> {
            AnuncioCompleto anuncio = dao.buscarCompleto(id);
            if (callback != null) {
                callback.onResultado(anuncio);
            }
        });
    }

    public void listarPorLocal(int idLocal, ResultadoCallback<List<AnuncioCompleto>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioCompleto> lista = dao.listarPorLocal(idLocal);
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }

    public void atualizar(Anuncio anuncio, ResultadoCallback<Void> callback){
        DatabaseExecutor.executor.execute(() -> {
            dao.atualizar(anuncio);
            if(callback != null){
                callback.onResultado(null);
            }
        });
    }

    public void contarPorUtilizador(int idUtilizador, ResultadoCallback<Integer> callback){
        DatabaseExecutor.executor.execute(() -> {
            int total = dao.contarPorUtilizador(idUtilizador);
            if (callback != null) {
                callback.onResultado(total);
            }
        });
    }

    public void listarPorUtilizador(int idUtilizador, ResultadoCallback<List<Anuncio>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<Anuncio> lista = dao.listarPorUtilizador(idUtilizador);
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }
}
