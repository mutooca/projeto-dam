package ao.uan.fc.dam.mobile.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.security.SessionManager;

public class LocaisViewModel extends AndroidViewModel {
    private final LocalRepository repository;
    private final LiveData<List<Local>> locales;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LocaisViewModel(@NonNull Application application) {
        super(application);
        this.repository = new LocalRepository(application);
        this.locales = repository.getLocales();
    }

    public LiveData<List<Local>> getLocales() {
        return locales;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createLocal(String nome, Double lat, Double lon, Integer raio) {
        String email = SessionManager.getEmail(getApplication());
        if (email == null) {
            errorMessage.setValue("Sessão expirada. Faça login novamente.");
            return;
        }

        isLoading.setValue(true);
        Map<String, Object> request = new HashMap<>();
        request.put("nome", nome);
        request.put("latitude", lat);
        request.put("longitude", lon);
        request.put("raio", raio);
        request.put("criadorEmail", email); // Adicionado para identificar o autor
        
        // Coordenadas para validação de proximidade no backend
        request.put("latUtilizador", lat);
        request.put("lonUtilizador", lon);

        repository.createLocal(request, new LocalRepository.RepoCallback<Local>() {
            @Override
            public void onSuccess(Local result) {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void removeLocal(Local local) {
        String email = SessionManager.getEmail(getApplication());
        if (email == null) return;

        isLoading.setValue(true);
        repository.deleteLocal(local.getIdLocal(), email, new LocalRepository.RepoCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
