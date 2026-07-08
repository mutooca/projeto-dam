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
import ao.uan.fc.dam.mobile.security.KerberosManager;
import ao.uan.fc.dam.mobile.security.SessionManager;

/**
 * Arquiteto: Sincronização em Paralelo Otimizada
 * Utiliza o pool de threads centralizado para maximizar o desempenho.
 */
public class SyncRepository {
    private static final String TAG = "SyncRepository";
    private final Context context;
    private final AppDatabase db;
    private final AppExecutors executors;

    public SyncRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public CompletableFuture<Void> syncAllData(String email) {
        Log.d(TAG, "Sync paralelo iniciado para: " + email);

        // ⭐ OBTER CREDENCIAIS KERBEROS ⭐
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);

        if (ticket == null || sessionKey == null) {
            Log.e(TAG, "Sessão Kerberos inválida. Sync abortado.");
            return CompletableFuture.completedFuture(null);
        }

        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);
        if (authenticator == null) {
            Log.e(TAG, "Erro ao gerar autenticador. Sync abortado.");
            return CompletableFuture.completedFuture(null);
        }

        Log.d(TAG, "📤 Sync com Kerberos");
        Log.d(TAG, "   Ticket: " + ticket.substring(0, Math.min(30, ticket.length())) + "...");
        Log.d(TAG, "   Authenticator: " + authenticator.substring(0, Math.min(30, authenticator.length())) + "...");

        final String finalTicket = ticket;
        final String finalAuthenticator = authenticator;

        // Task 1: Perfil (com Kerberos)
        CompletableFuture<Void> profileTask = CompletableFuture.runAsync(() -> {
            try {
                SaldoResponse response = RetrofitClient.getInstance().getApi()
                        .obterSaldo(finalTicket, finalAuthenticator, email)
                        .execute()
                        .body();
                if (response != null) {
                    Utilizador user = new Utilizador();
                    user.setNome(response.getNome());
                    user.setEmail(response.getEmail());
                    user.setSaldo(response.getSaldoGlobal());
                    user.setTotalAnuncios(response.getTotalAnuncios());
                    user.setTotalEntregas(response.getTotalEntregas());
                    user.setPreferenciaAnuncio(response.getPreferencias());
                    db.utilizadorDao().deleteProfile();
                    db.utilizadorDao().insert(user);
                    Log.d(TAG, "✅ Perfil sincronizado: " + user.getEmail());
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao sincronizar perfil", e);
            }
        }, executors.networkIO());

        // Task 2: Locais (público - sem Kerberos)
        CompletableFuture<Void> localesTask = CompletableFuture.runAsync(() -> {
            try {
                List<Local> response = RetrofitClient.getInstance().getApi()
                        .listarLocais(0.0, 0.0)
                        .execute()
                        .body();
                if (response != null) {
                    db.localDao().deleteAll();
                    db.localDao().insertAll(response);
                    Log.d(TAG, "✅ Locais sincronizados: " + response.size());
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao sincronizar locais", e);
            }
        }, executors.networkIO());

        // Task 3: Anúncios (com Kerberos)
        CompletableFuture<Void> adsTask = CompletableFuture.runAsync(() -> {
            try {
                List<Anuncio> response = RetrofitClient.getInstance().getApi()
                        .listarMinhasMensagens(finalTicket, finalAuthenticator, email)
                        .execute()
                        .body();
                if (response != null) {
                    db.anuncioDao().deleteAll();
                    for (Anuncio a : response) {
                        a.setUsuarioEmail(email);
                    }
                    db.anuncioDao().insertAll(response);
                    Log.d(TAG, "✅ Anúncios sincronizados: " + response.size());
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao sincronizar anúncios", e);
            }
        }, executors.networkIO());

        return CompletableFuture.allOf(profileTask, localesTask, adsTask);
    }
}