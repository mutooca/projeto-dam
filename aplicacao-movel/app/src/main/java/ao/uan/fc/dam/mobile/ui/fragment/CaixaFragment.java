package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.AnuncioAdapter;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaixaFragment extends Fragment {
    private final List<Anuncio> anuncios = new ArrayList<>();
    private AnuncioAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtBadge;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String CATEGORIA = "HISTORICO";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caixa2, container, false);

        db = AppDatabase.getInstance(requireContext());
        progressBar = view.findViewById(R.id.progressCaixa);
        txtBadge = view.findViewById(R.id.txtBadge);
        RecyclerView recyclerViewCaixa = view.findViewById(R.id.recyclerViewCaixa);
        
        recyclerViewCaixa.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCaixa.setHasFixedSize(true);

        adapter = new AnuncioAdapter(anuncios, anuncio -> {
            VerAnuncioFragment fragment = VerAnuncioFragment.novaInstancia(anuncio);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerViewCaixa.setAdapter(adapter);

        carregarDadosCache();
        carregarCaixaServidor();
        return view;
    }

    private void carregarDadosCache() {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;

        executor.execute(() -> {
            List<Anuncio> cache = db.anuncioDao().listarPorCategoria(email, CATEGORIA);
            mainHandler.post(() -> {
                if (!cache.isEmpty() && anuncios.isEmpty()) {
                    adapter.atualizar(cache);
                    if (txtBadge != null) {
                        txtBadge.setText(cache.size() + " anúncios (offline)");
                    }
                }
            });
        });
    }

    private void carregarCaixaServidor() {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi().listarHistorico(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Anuncio> lista = response.body();
                    for (Anuncio a : lista) {
                        a.setUsuarioEmail(email);
                        a.setCategoria(CATEGORIA);
                    }
                    adapter.atualizar(lista);
                    if (txtBadge != null) {
                        txtBadge.setText(lista.size() + " anúncios");
                    }
                    
                    executor.execute(() -> {
                        db.anuncioDao().limparPorCategoria(email, CATEGORIA);
                        db.anuncioDao().salvarTodos(lista);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "A exibir dados offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
