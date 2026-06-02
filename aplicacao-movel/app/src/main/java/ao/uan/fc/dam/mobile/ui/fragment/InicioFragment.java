package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.AnuncioAdapter;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {
    private static final String CATEGORIA = "MINHAS_MENSAGENS";
    private AnuncioAdapter adapter;
    private AppDatabase db;
    private final List<Anuncio> anuncios = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        this.db = AppDatabase.getInstance(requireContext());
        RecyclerView inicioRecyclerView = view.findViewById(R.id.recyclerViewInicio);
        inicioRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        inicioRecyclerView.setHasFixedSize(true);
        
        this.adapter = new AnuncioAdapter(this.anuncios, anuncio -> {
            VerAnuncioFragment fragment = VerAnuncioFragment.novaInstancia(anuncio);
            getParentFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .addToBackStack(null)
                .commit();
        });
        
        inicioRecyclerView.setAdapter(this.adapter);
        carregarDadosCache();
        carregarAnunciosServidor();
        return view;
    }

    private void carregarDadosCache() {
        final String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;
        
        this.executor.execute(() -> {
            final List<Anuncio> cache = this.db.anuncioDao().listarPorCategoria(email, CATEGORIA);
            this.mainHandler.post(() -> {
                if (!cache.isEmpty() && this.anuncios.isEmpty()) {
                    this.adapter.atualizar(cache);
                }
            });
        });
    }

    private void carregarAnunciosServidor() {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;

        RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(@NonNull Call<List<Anuncio>> call, @NonNull Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<Anuncio> lista = response.body();
                    for (Anuncio a : lista) {
                        a.setUsuarioEmail(email);
                        a.setCategoria(CATEGORIA);
                    }
                    InicioFragment.this.adapter.atualizar(lista);
                    InicioFragment.this.executor.execute(() -> {
                        InicioFragment.this.db.anuncioDao().limparPorCategoria(email, CATEGORIA);
                        InicioFragment.this.db.anuncioDao().salvarTodos(lista);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Anuncio>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "A exibir mensagens offline", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.executor.shutdown();
    }
}
