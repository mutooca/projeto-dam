package ao.uan.fc.dam.mobile.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.security.SessionManager;

public class PerfilViewModel extends AndroidViewModel {
    private final UtilizadorRepository repository;
    private final LiveData<Utilizador> profile;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UtilizadorRepository(application);
        this.profile = repository.getProfile(SessionManager.getEmail(application));
    }

    public LiveData<Utilizador> getProfile() {
        return profile;
    }

    public void updateNameLocal(String nome) {
        repository.updateLocalName(nome);
    }

    public void updatePrefsLocal(String prefs) {
        repository.updateLocalPreferences(prefs);
    }

    public void refresh() {
        repository.refreshProfile(SessionManager.getEmail(getApplication()));
    }
}
