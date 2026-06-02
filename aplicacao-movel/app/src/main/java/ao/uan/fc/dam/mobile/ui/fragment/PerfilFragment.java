package ao.uan.fc.dam.mobile.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.PerfilAdapter;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Historico;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {
    private static final String CATEGORIA = "HISTORICO";
    private PerfilAdapter adapter;
    private ImageView btnEditarPerfil;
    private AppDatabase db;
    private ProgressBar progressBar;
    private TextView txtEmail;
    private TextView txtNome;
    private TextView txtSaldo;
    private final List<Historico> historicos = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        this.db = AppDatabase.getInstance(requireContext());
        this.txtNome = view.findViewById(R.id.txtNome);
        this.txtEmail = view.findViewById(R.id.txtEmail);
        this.txtSaldo = view.findViewById(R.id.txtSaldo);
        this.btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        this.progressBar = view.findViewById(R.id.progressPerfil);
        RecyclerView recyclerHistorico = view.findViewById(R.id.recyclerHistorico);
        recyclerHistorico.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.adapter = new PerfilAdapter(this.historicos);
        recyclerHistorico.setAdapter(this.adapter);
        
        MaterialButton terminarSessao = view.findViewById(R.id.btnTerminarSessao);
        if (terminarSessao != null) {
            terminarSessao.setOnClickListener(v -> terminarSessao());
        }
        
        if (this.btnEditarPerfil != null) {
            this.btnEditarPerfil.setOnClickListener(v -> mostrarDialogEditarPerfil());
        }
        
        preencherSessao();
        carregarHistoricoCache();
        carregarSaldo();
        carregarHistoricoServidor();
        return view;
    }

    private void carregarHistoricoCache() {
        final String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;
        
        this.executor.execute(() -> {
            final List<Anuncio> cache = this.db.anuncioDao().listarPorCategoria(email, CATEGORIA);
            this.mainHandler.post(() -> {
                if (!cache.isEmpty() && this.historicos.isEmpty()) {
                    List<Historico> lista = new ArrayList<>();
                    for (Anuncio anuncio : cache) {
                        lista.add(new Historico("Anuncio visualizado (offline)", anuncio.getPontos(), anuncio.getTitulo(), anuncio.getData_publicacao()));
                    }
                    this.adapter.atualizar(lista);
                }
            });
        });
    }

    private void mostrarDialogEditarPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Perfil");
        final EditText input = new EditText(requireContext());
        input.setHint("Novo nome");
        input.setText(this.txtNome.getText().toString());
        builder.setView(input);
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = input.getText().toString().trim();
            if (!novoNome.isEmpty()) {
                atualizarPerfil(novoNome);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void atualizarPerfil(String novoNome) {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("nome", novoNome);
        RetrofitClient.getInstance().getApi().editarPerfil(request).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(@NonNull Call<Utilizador> call, @NonNull Response<Utilizador> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    txtNome.setText(response.body().getNome());
                    Toast.makeText(requireContext(), "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Utilizador> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherSessao() {
        String email = KerberosAuthManager.getEmail(requireContext());
        this.txtNome.setText("Carregando...");
        this.txtEmail.setText(email == null ? "" : email);
    }

    private void carregarSaldo() {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;
        
        RetrofitClient.getInstance().getApi().obterSaldo(email).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(@NonNull Call<Utilizador> call, @NonNull Response<Utilizador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtNome.setText(response.body().getNome());
                    txtEmail.setText(response.body().getEmail());
                    txtSaldo.setText(((int) response.body().getSaldo()) + " pts");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Utilizador> call, @NonNull Throwable t) {}
        });
    }

    private void carregarHistoricoServidor() {
        String email = KerberosAuthManager.getEmail(requireContext());
        if (email == null) return;
        
        RetrofitClient.getInstance().getApi().listarHistorico(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(@NonNull Call<List<Anuncio>> call, @NonNull Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<Anuncio> anunciosServidor = response.body();
                    List<Historico> lista = new ArrayList<>();
                    for (Anuncio anuncio : anunciosServidor) {
                        anuncio.setUsuarioEmail(email);
                        anuncio.setCategoria(CATEGORIA);
                        lista.add(new Historico("Anuncio visualizado", anuncio.getPontos(), anuncio.getTitulo(), anuncio.getData_publicacao()));
                    }
                    adapter.atualizar(lista);
                    executor.execute(() -> {
                        db.anuncioDao().limparPorCategoria(email, CATEGORIA);
                        db.anuncioDao().salvarTodos(anunciosServidor);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Anuncio>> call, @NonNull Throwable t) {}
        });
    }

    private void terminarSessao() {
        KerberosAuthManager.clear(requireContext());
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.executor.shutdown();
    }
}
