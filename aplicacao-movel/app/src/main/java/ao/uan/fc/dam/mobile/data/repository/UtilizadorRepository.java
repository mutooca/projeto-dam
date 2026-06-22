package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.database.UtilizadorDao;
import ao.uan.fc.dam.mobile.model.SaldoResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilizadorRepository {
    private final UtilizadorDao utilizadorDao;
    private final AppExecutors executors;
    private final MutableLiveData<Utilizador> userLiveData = new MutableLiveData<>();

    public UtilizadorRepository(Context context) {
        this.utilizadorDao = AppDatabase.getInstance(context).utilizadorDao();
        this.executors = AppExecutors.getInstance();
    }

    public LiveData<Utilizador> getProfile(String email) {
        refreshProfile(email);
        return userLiveData;
    }

    public void updateLocalName(String nome) {
        executors.diskIO().execute(() -> {
            utilizadorDao.updateName(nome);
            loadFromCache();
        });
    }

    public void updateLocalPreferences(String prefs) {
        executors.diskIO().execute(() -> {
            utilizadorDao.updatePreferences(prefs);
            loadFromCache();
        });
    }

    private void loadFromCache() {
        executors.diskIO().execute(() -> {
            Utilizador cached = utilizadorDao.getProfile();
            if (cached != null) userLiveData.postValue(cached);
        });
    }

    public void refreshProfile(String email) {
        // Cache First
        loadFromCache();

        // Sync from Server
        RetrofitClient.getInstance().getApi().obterSaldo(email).enqueue(new Callback<SaldoResponse>() {
            @Override
            public void onResponse(Call<SaldoResponse> call, Response<SaldoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executors.diskIO().execute(() -> {
                        SaldoResponse r = response.body();
                        Utilizador existing = utilizadorDao.getProfile();
                        
                        Utilizador user = new Utilizador();
                        // Reutiliza o ID local para manter integridade do Room
                        if (existing != null) {
                            user.setIdUtilizador(existing.getIdUtilizador());
                        }
                        
                        user.setNome(r.getNome());
                        user.setEmail(r.getEmail());
                        user.setSaldo(r.getSaldoGlobal());
                        user.setTotalAnuncios(r.getTotalAnuncios());
                        user.setTotalEntregas(r.getTotalEntregas());

                        // Lógica de Preservação: Não apaga propriedades locais se o servidor retornar null
                        String serverPrefs = r.getPreferencias();
                        if ((serverPrefs == null || serverPrefs.isEmpty()) && existing != null) {
                            user.setPreferenciaAnuncio(existing.getPreferenciaAnuncio());
                        } else {
                            user.setPreferenciaAnuncio(serverPrefs);
                        }
                        
                        utilizadorDao.deleteProfile();
                        utilizadorDao.insert(user);
                        userLiveData.postValue(user);
                    });
                }
            }
            @Override public void onFailure(Call<SaldoResponse> call, Throwable t) {
                Log.e("UtilizadorRepo", "Sync falhou, mantendo dados offline.");
            }
        });
    }
}
