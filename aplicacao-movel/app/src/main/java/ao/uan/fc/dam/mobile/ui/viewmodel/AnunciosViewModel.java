package ao.uan.fc.dam.mobile.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;

public class AnunciosViewModel extends AndroidViewModel {
    private final AnuncioRepository repository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<Anuncio>> localAds = new MutableLiveData<>();

    public AnunciosViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AnuncioRepository(application);
    }

    public LiveData<List<Anuncio>> getMeusAnuncios() {
        return repository.getMeusAnuncios(SessionManager.getEmail(getApplication()));
    }

    public LiveData<List<Anuncio>> getLocalAds() {
        return localAds;
    }

    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void postar(String localId, String titulo, String conteudo, String politica, String restricoes, String modoEntrega) {
        isLoading.setValue(true);
        Map<String, Object> req = new java.util.HashMap<>();
        req.put("emailAutor", SessionManager.getEmail(getApplication()));
        req.put("localId", localId);
        req.put("titulo", titulo);
        req.put("conteudo", conteudo);
        req.put("categoria", "GERAL");
        
        // Requisitos 2.1.3 e 2.1.4
        req.put("tipo_politica", politica);
        req.put("restricoes_perfil", restricoes);
        req.put("modo_entrega", modoEntrega);
        // Janela de tempo baseline (pode ser expandida com DatePickers)
        req.put("janela_inicio", java.time.LocalDateTime.now().toString());
        req.put("janela_fim", java.time.LocalDateTime.now().plusDays(7).toString());

        repository.postarAnuncio(req, new AnuncioRepository.RepoCallback<Anuncio>() {
            @Override
            public void onSuccess(Anuncio result) {
                isLoading.postValue(false);
                refresh();
            }
            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void remover(UUID id) {
        isLoading.setValue(true);
        repository.removerAnuncio(id, SessionManager.getEmail(getApplication()), new AnuncioRepository.RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
                refresh();
            }
            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void filtrarPorLocal(UUID localId) {
        isLoading.setValue(true);
        repository.listarPorLocal(localId, new AnuncioRepository.RepoCallback<List<Anuncio>>() {
            @Override
            public void onSuccess(List<Anuncio> result) {
                isLoading.postValue(false);
                localAds.postValue(result);
            }
            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void refresh() {
        repository.refreshMeusAnuncios(SessionManager.getEmail(getApplication()));
    }
}
