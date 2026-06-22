package ao.uan.fc.dam.mobile.data;

import android.content.Context;
import android.util.Log;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.SaldoResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;

/**
 * Arquiteto: Sincronização em Paralelo Otimizada
 * Utiliza o pool de threads centralizado para maximizar o desempenho.
 */
public class SyncRepository {
    private static final String TAG = "SyncRepository";
    private final AppDatabase db;
    private final AppExecutors executors;

    public SyncRepository(Context context) {
        this.db = AppDatabase.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public CompletableFuture<Void> syncAllData(String email) {
        Log.d(TAG, "Sync paralelo iniciado...");

        // Task 1: Perfil (Thread Pool de Rede)
        CompletableFuture<Void> profileTask = CompletableFuture.runAsync(() -> {
            try {
                SaldoResponse response = RetrofitClient.getInstance().getApi().obterSaldo(email).execute().body();
                if (response != null) {
                    Utilizador user = new Utilizador();
                    user.setNome(response.getNome()); user.setEmail(response.getEmail()); user.setSaldo(response.getSaldoGlobal());
                    db.utilizadorDao().deleteProfile();
                    db.utilizadorDao().insert(user);
                }
            } catch (Exception e) { Log.e(TAG, "Erro perfil", e); }
        }, executors.networkIO());

        // Task 2: Locais
        CompletableFuture<Void> localesTask = CompletableFuture.runAsync(() -> {
            try {
                List<Local> response = RetrofitClient.getInstance().getApi().listarLocais(0.0, 0.0).execute().body();
                if (response != null) {
                    db.localDao().deleteAll();
                    db.localDao().insertAll(response);
                }
            } catch (Exception e) { Log.e(TAG, "Erro locais", e); }
        }, executors.networkIO());

        // Task 3: Anúncios
        CompletableFuture<Void> adsTask = CompletableFuture.runAsync(() -> {
            try {
                List<Anuncio> response = RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).execute().body();
                if (response != null) {
                    db.anuncioDao().deleteAll();
                    db.anuncioDao().insertAll(response);
                }
            } catch (Exception e) { Log.e(TAG, "Erro anúncios", e); }
        }, executors.networkIO());

        return CompletableFuture.allOf(profileTask, localesTask, adsTask);
    }
}
