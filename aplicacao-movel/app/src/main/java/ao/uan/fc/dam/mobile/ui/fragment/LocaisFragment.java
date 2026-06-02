package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.LocalAdapter;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Local;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocaisFragment extends Fragment {
    private LocalAdapter adapter;
    private AppDatabase db;
    private ProgressBar progressBar;
    private final List<Local> locais = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locais, container, false);
        this.db = AppDatabase.getInstance(requireContext());
        this.progressBar = view.findViewById(R.id.progressLocais);
        RecyclerView localRecyclerView = view.findViewById(R.id.recyclerLocais);
        localRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        localRecyclerView.setHasFixedSize(true);
        this.adapter = new LocalAdapter(this.locais);
        localRecyclerView.setAdapter(this.adapter);
        carregarDadosCache();
        carregarLocaisServidor();
        return view;
    }

    private void carregarDadosCache() {
        this.executor.execute(() -> {
            final List<Local> cache = this.db.localDao().listarTodos();
            this.mainHandler.post(() -> {
                if (!cache.isEmpty() && this.locais.isEmpty()) {
                    this.adapter.atualizar(cache);
                }
            });
        });
    }

    private void carregarLocaisServidor() {
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        RetrofitClient.getInstance().getApi().listarInfraestruturas(0.0d, 0.0d, 20).enqueue(new Callback<List<Local>>() {
            @Override
            public void onResponse(@NonNull Call<List<Local>> call, @NonNull Response<List<Local>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    final List<Local> novosLocais = response.body();
                    adapter.atualizar(novosLocais);
                    executor.execute(() -> {
                        db.localDao().limparTudo();
                        db.localDao().salvarTodos(novosLocais);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Local>> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (isAdded()) {
                    Toast.makeText(requireContext(), "A exibir locais offline", Toast.LENGTH_SHORT).show();
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
