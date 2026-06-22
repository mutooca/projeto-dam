package ao.uan.fc.dam.mobile.data.local;

import android.content.Context;
import android.util.Log;

import java.util.List;

import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.SaldoResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;

/**
 * Arquiteto: Motor de Cache Local (SSOT - Single Source of Truth)
 * Centraliza a lógica de persistência e sincronização de dados globais.
 */
public class CacheManager {
    private static final String TAG = "CacheManager";
    private final AppDatabase db;
    private final AppExecutors executors;
    private static CacheManager instance;

    private CacheManager(Context context) {
        this.db = AppDatabase.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }

    public void syncAll(String email) {
        Log.i(TAG, "Iniciando sincronização global em background para " + email);
        fetchUser(email);
        fetchLocales();
        fetchAds(email);
    }

    private void fetchUser(String email) {
        RetrofitClient.getInstance().getApi().obterSaldo(email).enqueue(new retrofit2.Callback<SaldoResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SaldoResponse> call, retrofit2.Response<SaldoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> {
                        SaldoResponse r = response.body();
                        Utilizador existing = db.utilizadorDao().getProfile();
                        
                        Utilizador u = new Utilizador();
                        if (existing != null) u.setIdUtilizador(existing.getIdUtilizador());
                        
                        u.setNome(r.getNome()); 
                        u.setEmail(r.getEmail()); 
                        u.setSaldo(r.getSaldoGlobal());
                        u.setTotalAnuncios(r.getTotalAnuncios());
                        u.setTotalEntregas(r.getTotalEntregas());

                        String serverPrefs = r.getPreferencias();
                        if ((serverPrefs == null || serverPrefs.isEmpty()) && existing != null) {
                            u.setPreferenciaAnuncio(existing.getPreferenciaAnuncio());
                        } else {
                            u.setPreferenciaAnuncio(serverPrefs);
                        }
                        
                        db.utilizadorDao().deleteProfile();
                        db.utilizadorDao().insert(u);
                    });
                }
            }
            @Override public void onFailure(retrofit2.Call<SaldoResponse> call, Throwable t) {}
        });
    }

    private void fetchLocales() {
        RetrofitClient.getInstance().getApi().listarLocais(0.0, 0.0).enqueue(new retrofit2.Callback<List<Local>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Local>> call, retrofit2.Response<List<Local>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> {
                        db.localDao().deleteAll();
                        db.localDao().insertAll(response.body());
                    });
                }
            }
            @Override public void onFailure(retrofit2.Call<List<Local>> call, Throwable t) {}
        });
    }

    private void fetchAds(String email) {
        RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).enqueue(new retrofit2.Callback<List<Anuncio>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Anuncio>> call, retrofit2.Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> {
                        List<Anuncio> ads = response.body();
                        for(Anuncio a : ads) a.setUsuarioEmail(email);
                        db.anuncioDao().deleteAll();
                        db.anuncioDao().insertAll(ads);
                    });
                }
            }
            @Override public void onFailure(retrofit2.Call<List<Anuncio>> call, Throwable t) {}
        });
    }
}
