package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.core.AppExecutors;
import ao.uan.fc.dam.mobile.database.AnuncioDao;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.security.KerberosManager;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.service.DecentralizedManager;
import ao.uan.fc.dam.mobile.service.PolicyMatcher;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnuncioRepository {
    private static final String TAG = "AnuncioRepository";
    private final Context context;
    private final AnuncioDao anuncioDao;
    private final AppDatabase db;
    private final AppExecutors executors;
    private final MutableLiveData<List<Anuncio>> anunciosLiveData = new MutableLiveData<>();
    private long lastFetchTime = 0;
    private static final long FETCH_THRESHOLD = 60000;

    public AnuncioRepository(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.getInstance(context);
        this.anuncioDao = db.anuncioDao();
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
            anunciosLiveData.postValue(applyPolicyFilter(cached));
        });
    }

    private List<Anuncio> applyPolicyFilter(List<Anuncio> ads) {
        if (ads == null) return new ArrayList<>();
        Utilizador perfil = db.utilizadorDao().getProfile();
        if (perfil == null) return ads;

        List<Anuncio> filtered = new ArrayList<>();
        for (Anuncio ad : ads) {
            Map<String, String> restrictions = parseRestricoes(ad.getRestricaoPerfil());
            Map<String, String> whitelist = "BLACKLIST".equalsIgnoreCase(ad.getTipoPolitica()) ? null : restrictions;
            Map<String, String> blacklist = "BLACKLIST".equalsIgnoreCase(ad.getTipoPolitica()) ? restrictions : null;
            if (PolicyMatcher.match(perfil.getAtributos(), whitelist, blacklist)) {
                filtered.add(ad);
            }
        }
        return filtered;
    }

    private Map<String, String> parseRestricoes(String str) {
        Map<String, String> map = new HashMap<>();
        if (str != null && !str.isBlank()) {
            for (String pair : str.split(",")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) map.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }
        return map;
    }

    public void refreshMeusAnuncios(String email) {
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);

        RetrofitClient.getInstance().getApi().listarMinhasMensagens(ticket, authenticator, email)
                .enqueue(new Callback<List<Anuncio>>() {
                    @Override
                    public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            lastFetchTime = System.currentTimeMillis();
                            executors.diskIO().execute(() -> {
                                anuncioDao.deleteSyncedAds();
                                for(Anuncio a : response.body()) a.setUsuarioEmail(email);
                                anuncioDao.insertAll(response.body());
                                anunciosLiveData.postValue(applyPolicyFilter(anuncioDao.getAll()));
                            });
                        }
                    }
                    @Override public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                        Log.e(TAG, "Falha ao sincronizar anúncios", t);
                    }
                });
    }

    // ============================================================
    // ⭐ POSTAR ANÚNCIO (CORRIGIDO COM KERBEROS) ⭐
    // ============================================================
    public void postarAnuncio(Map<String, Object> request, RepoCallback<Anuncio> callback) {
        String modoEntrega = String.valueOf(request.get("modoEntrega"));
        if ("DESCENTRALIZADO".equalsIgnoreCase(modoEntrega)) {
            postarAnuncioDescentralizado(request, callback);
            return;
        }

        // ⭐ OBTER CREDENCIAIS KERBEROS ⭐
        String email = SessionManager.getEmail(context);
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);

        if (email == null || ticket == null || sessionKey == null) {
            callback.onError("Sessão inválida. Faça login novamente.");
            return;
        }

        // ⭐ GERAR AUTHENTICATOR ⭐
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);
        if (authenticator == null) {
            callback.onError("Erro ao gerar autenticador");
            return;
        }

        Log.d(TAG, "📤 Postar anúncio com Kerberos");
        Log.d(TAG, "   Ticket: " + ticket.substring(0, Math.min(30, ticket.length())) + "...");
        Log.d(TAG, "   Authenticator: " + authenticator.substring(0, Math.min(30, authenticator.length())) + "...");

        // ⭐ ENVIAR COM HEADERS KERBEROS ⭐
        RetrofitClient.getInstance().getApi()
                .postarAnuncio(ticket, authenticator, request)
                .enqueue(new Callback<Anuncio>() {
                    @Override
                    public void onResponse(Call<Anuncio> call, Response<Anuncio> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            lastFetchTime = 0;
                            executors.diskIO().execute(() -> anuncioDao.insertAll(List.of(response.body())));
                            callback.onSuccess(response.body());
                        } else {
                            String erro = "Erro no servidor: " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    erro = response.errorBody().string();
                                }
                            } catch (Exception e) {}
                            callback.onError(erro);
                        }
                    }
                    @Override public void onFailure(Call<Anuncio> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    private void postarAnuncioDescentralizado(Map<String, Object> request, RepoCallback<Anuncio> callback) {
        executors.diskIO().execute(() -> {
            Anuncio anuncio = new Anuncio();
            anuncio.setTitulo(String.valueOf(request.get("titulo")));
            anuncio.setConteudo(String.valueOf(request.get("conteudo")));
            anuncio.setCategoria(String.valueOf(request.get("categoria")));
            anuncio.setModo_entrega("DESCENTRALIZADO");
            anuncio.setTipoPolitica(String.valueOf(request.get("tipoPolitica")));
            anuncio.setRestricaoPerfil(String.valueOf(request.get("politicaFiltro")));
            anuncio.setAutorEmail(String.valueOf(request.get("emailAutor")));
            anuncio.setUsuarioEmail(String.valueOf(request.get("emailAutor")));
            anuncio.setEstado("ATIVO");
            anuncio.setDataPublicacao(LocalDateTime.now());
            anuncioDao.insertAll(List.of(anuncio));

            DecentralizedManager manager = new DecentralizedManager(context, anuncio.getAutorEmail());
            manager.start();
            manager.syncAdsWithNeighbors();

            callback.onSuccess(anuncio);
        });
    }

    public void listarPorLocal(UUID localId, RepoCallback<List<Anuncio>> callback) {
        // ⭐ NÃO ENVIA HEADERS - endpoint público ⭐
        RetrofitClient.getInstance().getApi()
                .listarPorLocal(localId.toString())
                .enqueue(new Callback<List<Anuncio>>() {
                    @Override
                    public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(applyPolicyFilter(response.body()));
                        } else {
                            callback.onError("Erro ao listar anúncios do local");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void removerAnuncio(UUID id, String email, RepoCallback<Void> callback) {
        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);

        RetrofitClient.getInstance().getApi()
                .removerAnuncio(ticket, authenticator, id.toString(), email)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        executors.diskIO().execute(() -> {
                            anuncioDao.deleteById(id);
                            loadFromCache();
                        });
                        callback.onSuccess(null);
                    }
                    @Override public void onFailure(Call<ResponseBody> call, Throwable t) { callback.onSuccess(null); }
                });
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}