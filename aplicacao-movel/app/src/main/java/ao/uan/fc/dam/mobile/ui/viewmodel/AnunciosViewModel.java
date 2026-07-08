package ao.uan.fc.dam.mobile.ui.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.KerberosManager;
import ao.uan.fc.dam.mobile.security.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnunciosViewModel extends ViewModel {

    private final MutableLiveData<List<Anuncio>> meusAnuncios = new MutableLiveData<>();
    private final MutableLiveData<List<Anuncio>> anunciosDisponiveis = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // ============================================================
    // ⭐ GETTERS ⭐
    // ============================================================
    public LiveData<List<Anuncio>> getMeusAnuncios() {
        return meusAnuncios;
    }

    public LiveData<List<Anuncio>> getAnunciosDisponiveis() {
        return anunciosDisponiveis;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // ============================================================
    // ⭐ CARREGAR MEUS ANÚNCIOS ⭐
    // ============================================================
    public void carregarMeusAnuncios(Context context, String email) {
        loading.setValue(true);
        errorMessage.setValue(null);

        String ticket = SessionManager.getTicket(context);
        String sessionKey = SessionManager.getSessionKey(context);
        String authenticator = KerberosManager.generateAuthenticator(email, sessionKey);

        if (ticket == null || authenticator == null) {
            loading.setValue(false);
            errorMessage.setValue("Erro de autenticação");
            return;
        }

        RetrofitClient.getInstance().getApi()
                .listarMinhasMensagens(ticket, authenticator, email)
                .enqueue(new Callback<List<Anuncio>>() {
                    @Override
                    public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                        loading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            meusAnuncios.setValue(response.body());
                        } else {
                            errorMessage.setValue("Erro ao carregar anúncios: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                        loading.setValue(false);
                        errorMessage.setValue("Falha na conexão: " + t.getMessage());
                    }
                });
    }

    // ============================================================
    // ⭐ CARREGAR ANÚNCIOS POR LOCAL ⭐
    // ============================================================
    public void carregarAnunciosPorLocal(Context context, UUID localId) {
        loading.setValue(true);
        errorMessage.setValue(null);

        AnuncioRepository repository = new AnuncioRepository(context);
        repository.listarPorLocal(localId, new AnuncioRepository.RepoCallback<List<Anuncio>>() {
            @Override
            public void onSuccess(List<Anuncio> result) {
                loading.setValue(false);
                anunciosDisponiveis.setValue(result);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // ============================================================
    // ⭐ POSTAR ANÚNCIO ⭐
    // ============================================================
    public void postarAnuncio(Context context, Map<String, Object> request, PostarCallback callback) {
        loading.setValue(true);
        errorMessage.setValue(null);

        AnuncioRepository repository = new AnuncioRepository(context);
        repository.postarAnuncio(request, new AnuncioRepository.RepoCallback<Anuncio>() {
            @Override
            public void onSuccess(Anuncio result) {
                loading.setValue(false);
                // Recarregar a lista após postar
                String email = SessionManager.getEmail(context);
                if (email != null) {
                    carregarMeusAnuncios(context, email);
                }
                callback.onSuccess();
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
                callback.onError(message);
            }
        });
    }

    // ============================================================
    // ⭐ REMOVER ANÚNCIO ⭐
    // ============================================================
    public void removerAnuncio(Context context, UUID anuncioId, RemoverCallback callback) {
        loading.setValue(true);
        errorMessage.setValue(null);

        String email = SessionManager.getEmail(context);
        if (email == null) {
            loading.setValue(false);
            errorMessage.setValue("Sessão inválida");
            callback.onError("Sessão inválida");
            return;
        }

        AnuncioRepository repository = new AnuncioRepository(context);
        repository.removerAnuncio(anuncioId, email, new AnuncioRepository.RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loading.setValue(false);
                // Recarregar a lista após remover
                carregarMeusAnuncios(context, email);
                callback.onSuccess();
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                errorMessage.setValue(message);
                callback.onError(message);
            }
        });
    }

    // ============================================================
    // ⭐ INTERFACES DE CALLBACK ⭐
    // ============================================================
    public interface PostarCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface RemoverCallback {
        void onSuccess();
        void onError(String message);
    }
}