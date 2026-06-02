package ao.uan.fc.dam.mobile.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostarFragment extends Fragment implements AdicionarLocal.OnLocalNameSubmitted {
    private FusedLocationProviderClient fusedLocationClient;
    private final List<Local> locais = new ArrayList<>();
    private Local localSelecionado;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private EditText mensagem;
    private String nomeLocalPendente;
    private ProgressBar progressBar;
    private MaterialButton publicar;
    private TextView selecionarLocal;
    private EditText titulo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        this.locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted && this.nomeLocalPendente != null) {
                criarLocalComCoordenadas(this.nomeLocalPendente);
            } else {
                Toast.makeText(requireContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postar, container, false);
        this.selecionarLocal = view.findViewById(R.id.txtSelecionarLocal);
        this.titulo = view.findViewById(R.id.edtTitulo);
        this.mensagem = view.findViewById(R.id.edtDescricao);
        this.publicar = view.findViewById(R.id.btnPublicarAnuncio);
        this.progressBar = view.findViewById(R.id.progressPostar);

        view.findViewById(R.id.txtAdicionarLocal).setOnClickListener(v -> abrirDialogAdicionarLocal());
        this.selecionarLocal.setOnClickListener(v -> mostrarDialogLocais());
        this.publicar.setOnClickListener(v -> postarMensagem());

        carregarLocais();
        return view;
    }

    @Override
    public void onLocalNameSubmitted(String nome) {
        this.nomeLocalPendente = nome;
        if (temPermissaoLocalizacao()) {
            criarLocalComCoordenadas(nome);
        } else {
            this.locationPermissionLauncher.launch("android.permission.ACCESS_FINE_LOCATION");
        }
    }

    private void abrirDialogAdicionarLocal() {
        AdicionarLocal dialog = new AdicionarLocal();
        dialog.setOnLocalNameSubmitted(this);
        dialog.show(getParentFragmentManager(), "AdicionarLocal");
    }

    private boolean temPermissaoLocalizacao() {
        return ContextCompat.checkSelfPermission(requireContext(), "android.permission.ACCESS_FINE_LOCATION") == 0 || 
               ContextCompat.checkSelfPermission(requireContext(), "android.permission.ACCESS_COARSE_LOCATION") == 0;
    }

    private void carregarLocais() {
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
                    locais.clear();
                    locais.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Local>> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Falha ao carregar locais", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarDialogLocais() {
        if (this.locais.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Sem locais")
                .setMessage("Ainda não existem locais disponíveis. Crie um local usando a sua localização atual.")
                .setPositiveButton("Criar local", (dialog, which) -> abrirDialogAdicionarLocal())
                .setNegativeButton("Cancelar", null)
                .show();
            return;
        }
        
        String[] nomes = new String[this.locais.size()];
        for (int i = 0; i < this.locais.size(); i++) {
            nomes[i] = this.locais.get(i).getNome();
        }
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Selecionar local")
            .setItems(nomes, (dialog, which) -> selecionarLocal(locais.get(which)))
            .setPositiveButton("Criar novo", (dialog, which) -> abrirDialogAdicionarLocal())
            .show();
    }

    private void criarLocalComCoordenadas(final String nome) {
        if (!temPermissaoLocalizacao()) {
            this.locationPermissionLauncher.launch("android.permission.ACCESS_FINE_LOCATION");
            return;
        }
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        CancellationTokenSource tokenSource = new CancellationTokenSource();
        try {
            this.fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        enviarLocal(nome, location);
                    } else {
                        obterUltimaLocalizacao(nome);
                    }
                })
                .addOnFailureListener(e -> obterUltimaLocalizacao(nome));
        } catch (SecurityException e) {
            if (this.progressBar != null) {
                this.progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(requireContext(), "Sem permissão de localização", Toast.LENGTH_SHORT).show();
        }
    }

    private void obterUltimaLocalizacao(final String nome) {
        try {
            this.fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        enviarLocal(nome, location);
                    } else {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(requireContext(), "Não foi possível obter a localização atual", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(requireContext(), "Falha ao obter localização", Toast.LENGTH_SHORT).show();
                });
        } catch (SecurityException e) {
            if (this.progressBar != null) {
                this.progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(requireContext(), "Sem permissão de localização", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarLocal(String nome, Location location) {
        Map<String, Object> request = new HashMap<>();
        request.put("nome", nome);
        request.put("latitude", location.getLatitude());
        request.put("longitude", location.getLongitude());
        request.put("capacidade", 100);
        
        RetrofitClient.getInstance().getApi().criarInfraestrutura(request).enqueue(new Callback<Local>() {
            @Override
            public void onResponse(@NonNull Call<Local> call, @NonNull Response<Local> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    Local novoLocal = response.body();
                    locais.add(0, novoLocal);
                    selecionarLocal(novoLocal);
                    nomeLocalPendente = null;
                    Toast.makeText(requireContext(), "Local criado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar local", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Local> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireContext(), "Falha na rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selecionarLocal(Local local) {
        this.localSelecionado = local;
        this.selecionarLocal.setText(local.getNome() == null ? "Local selecionado" : local.getNome());
    }

    private void postarMensagem() {
        String email = KerberosAuthManager.getEmail(requireContext());
        String tituloTexto = this.titulo.getText().toString().trim();
        String conteudoTexto = this.mensagem.getText().toString().trim();
        
        if (email == null) {
            Toast.makeText(requireContext(), "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.localSelecionado == null || this.localSelecionado.getId() == null) {
            Toast.makeText(requireContext(), "Selecione ou crie um local", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tituloTexto.isEmpty()) {
            this.titulo.setError("Título obrigatório");
            return;
        }
        if (conteudoTexto.isEmpty()) {
            this.mensagem.setError("Mensagem obrigatória");
            return;
        }

        this.publicar.setEnabled(false);
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("emailUtilizador", email);
        request.put("infraestruturaId", this.localSelecionado.getId());
        request.put("titulo", tituloTexto);
        request.put("conteudo", conteudoTexto);
        
        RetrofitClient.getInstance().getApi().postarMensagem(request).enqueue(new Callback<Anuncio>() {
            @Override
            public void onResponse(@NonNull Call<Anuncio> call, @NonNull Response<Anuncio> response) {
                publicar.setEnabled(true);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Anúncio publicado", Toast.LENGTH_SHORT).show();
                    titulo.setText("");
                    mensagem.setText("");
                    selecionarLocal.setText("Seleccionar local");
                    localSelecionado = null;
                } else {
                    Toast.makeText(requireContext(), "Erro ao publicar anúncio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Anuncio> call, @NonNull Throwable t) {
                publicar.setEnabled(true);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireContext(), "Falha na rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
