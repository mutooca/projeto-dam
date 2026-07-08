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
import ao.uan.fc.dam.mobile.security.KerberosManager;
import ao.uan.fc.dam.mobile.security.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Arquiteto: Motor de Cache Local (SSOT - Single Source of Truth)
 * Centraliza a lógica de persistência e sincronização de dados globais.
 */
public class CacheManager {
    private static final String TAG = "CacheManager";
    private final AppDatabase db;
    private final AppExecutors executors;
    private final Context context;
    private static CacheManager instance;

    private CacheManager(Context context) {
        this.context = context.getApplicationContext();
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

    // ============================================================
    // ⭐ FETCH USER (com Kerberos) ⭐
    // ============================================================
    private void fetchUser(String email) {
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);

        if (ticket == null || authenticator == null) {
            Log.e(TAG, "Credenciais Kerberos inválidas para fetchUser");
            return;
        }

        RetrofitClient.getInstance().getApi()
                .obterSaldo(ticket, authenticator, email)
                .enqueue(new Callback<SaldoResponse>() {
                    @Override
                    public void onResponse(Call<SaldoResponse> call, Response<SaldoResponse> response) {
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

                    @Override
                    public void onFailure(Call<SaldoResponse> call, Throwable t) {
                        Log.e(TAG, "Erro ao buscar utilizador: " + t.getMessage());
                    }
                });
    }

    // ============================================================
    // ⭐ FETCH LOCALES (público - sem autenticação) ⭐
    // ============================================================
    private void fetchLocales() {
        // Este endpoint pode ser público (sem Kerberos)
        RetrofitClient.getInstance().getApi()
                .listarLocais(0.0, 0.0)
                .enqueue(new Callback<List<Local>>() {
                    @Override
                    public void onResponse(Call<List<Local>> call, Response<List<Local>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            executors.diskIO().execute(() -> {
                                db.localDao().deleteAll();
                                db.localDao().insertAll(response.body());
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Local>> call, Throwable t) {
                        Log.e(TAG, "Erro ao buscar locais: " + t.getMessage());
                    }
                });
    }

    // ============================================================
    // ⭐ FETCH ADS (com Kerberos) ⭐
    // ============================================================
    private void fetchAds(String email) {
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);

        if (ticket == null || authenticator == null) {
            Log.e(TAG, "Credenciais Kerberos inválidas para fetchAds");
            return;
        }

        Log.d(TAG, "📤 Buscando anúncios com Kerberos para: " + email);
        Log.d(TAG, "   Ticket: " + ticket.substring(0, Math.min(30, ticket.length())) + "...");

        RetrofitClient.getInstance().getApi()
                .listarMinhasMensagens(ticket, authenticator, email)
                .enqueue(new Callback<List<Anuncio>>() {
                    @Override
                    public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            executors.diskIO().execute(() -> {
                                List<Anuncio> ads = response.body();
                                for (Anuncio a : ads) {
                                    a.setUsuarioEmail(email);
                                }
                                db.anuncioDao().deleteAll();
                                db.anuncioDao().insertAll(ads);
                                Log.d(TAG, "✅ " + ads.size() + " anúncios sincronizados");
                            });
                        } else {
                            Log.e(TAG, "Erro ao buscar anúncios: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                        Log.e(TAG, "Falha ao sincronizar anúncios: " + t.getMessage());
                    }
                });
    }

    // ============================================================
    // ⭐ MÉTODO PÚBLICO PARA FORÇAR SINCRONIZAÇÃO ⭐
    // ============================================================
    public void forceSync(String email) {
        syncAll(email);
    }

    // ============================================================
    // ⭐ MÉTODO PARA LIMPAR CACHE ⭐
    // ============================================================
    public void clearCache() {
        executors.diskIO().execute(() -> {
            db.utilizadorDao().deleteProfile();
            db.anuncioDao().deleteAll();
            db.localDao().deleteAll();
        });
        Log.i(TAG, "Cache local limpo");
    }
}