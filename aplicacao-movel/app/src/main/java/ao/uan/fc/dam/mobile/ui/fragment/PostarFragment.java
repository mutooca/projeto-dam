package ao.uan.fc.dam.mobile.ui.fragment;  // ← ESTA LINHA ESTAVA FALTANDO!

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;
import ao.uan.fc.dam.mobile.ui.viewmodel.LocaisViewModel;

public class PostarFragment extends Fragment {

    // ============================================================
    // 1. DECLARAÇÃO DE VARIÁVEIS
    // ============================================================

    private Spinner spinnerLocais;
    private EditText inputTitulo;
    private EditText inputMensagem;
    private EditText inputRestricoes;
    private RadioGroup radioGroupPolitica;
    private RadioGroup radioGroupEntrega;
    private Button btnPublicar;

    private AnunciosViewModel anunciosViewModel;
    private LocaisViewModel locaisViewModel;

    private final List<Local> locais = new ArrayList<>();
    private String modoEntregaSelecionado = "CENTRALIZADO";

    // ============================================================
    // 2. CICLO DE VIDA DO FRAGMENTO
    // ============================================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postar, container, false);

        anunciosViewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
        locaisViewModel = new ViewModelProvider(this).get(LocaisViewModel.class);

        spinnerLocais = view.findViewById(R.id.spinnerLocais);
        inputTitulo = view.findViewById(R.id.inputTitulo);
        inputMensagem = view.findViewById(R.id.inputMensagem);
        inputRestricoes = view.findViewById(R.id.inputRestricoes);
        radioGroupPolitica = view.findViewById(R.id.radioGroupPolitica);
        radioGroupEntrega = view.findViewById(R.id.radioGroupEntrega);
        btnPublicar = view.findViewById(R.id.btnPublicar);

        configurarListeners();
        carregarLocais();

        return view;
    }

    // ============================================================
    // 3. CONFIGURAÇÃO DE LISTENERS
    // ============================================================

    private void configurarListeners() {
        radioGroupEntrega.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCentralizado) {
                modoEntregaSelecionado = "CENTRALIZADO";
            } else if (checkedId == R.id.radioDescentralizado) {
                modoEntregaSelecionado = "DESCENTRALIZADO";
            }
            System.out.println("📡 Modo de entrega selecionado: " + modoEntregaSelecionado);
        });

        btnPublicar.setOnClickListener(v -> publicarAnuncio());
    }

    // ============================================================
    // 4. CARREGAR LOCAIS
    // ============================================================

    private void carregarLocais() {
        locaisViewModel.getLocales().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null && !lista.isEmpty()) {
                locais.clear();
                locais.addAll(lista);

                List<String> nomes = new ArrayList<>();
                for (Local l : lista) {
                    nomes.add(l.getNome());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        nomes
                );
                spinnerLocais.setAdapter(adapter);
            }
        });
    }

    // ============================================================
    // 5. PUBLICAR ANÚNCIO
    // ============================================================

    private void publicarAnuncio() {
        int posicaoLocal = spinnerLocais.getSelectedItemPosition();
        String titulo = inputTitulo.getText().toString().trim();
        String mensagem = inputMensagem.getText().toString().trim();
        String restricoes = inputRestricoes.getText().toString().trim();

        if (posicaoLocal < 0 || locais.isEmpty()) {
            Toast.makeText(requireContext(), "Selecione um local", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "Digite o título do anúncio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mensagem.isEmpty()) {
            Toast.makeText(requireContext(), "Digite o conteúdo do anúncio", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = SessionManager.getEmail(requireContext());
        if (email == null || email.isEmpty()) {
            Toast.makeText(requireContext(), "Faça login primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoPolitica = null;
        int selectedPolitica = radioGroupPolitica.getCheckedRadioButtonId();
        if (selectedPolitica == R.id.radioWhitelist) {
            tipoPolitica = "WHITELIST";
        } else if (selectedPolitica == R.id.radioBlacklist) {
            tipoPolitica = "BLACKLIST";
        }

        Map<String, Object> request = new HashMap<>();
        request.put("emailAutor", email);
        request.put("nomeLocal", locais.get(posicaoLocal).getNome());
        request.put("titulo", titulo);
        request.put("conteudo", mensagem);
        request.put("categoria", "GERAL");

        if (tipoPolitica != null) {
            request.put("tipoPolitica", tipoPolitica);
        }

        if (!restricoes.isEmpty()) {
            request.put("politicaFiltro", restricoes);
        }

        request.put("modoEntrega", modoEntregaSelecionado);

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("📤 ENVIANDO ANÚNCIO PARA O SERVIDOR");
        System.out.println("   Email: " + email);
        System.out.println("   Local: " + locais.get(posicaoLocal).getNome());
        System.out.println("   Título: " + titulo);
        System.out.println("   Conteúdo: " + (mensagem.length() > 50 ? mensagem.substring(0, 50) + "..." : mensagem));
        System.out.println("   Categoria: GERAL");
        System.out.println("   Política: " + tipoPolitica);
        System.out.println("   Restrições: " + restricoes);
        System.out.println("   Modo Entrega (interno): " + modoEntregaSelecionado);
        System.out.println("═══════════════════════════════════════════════════════════");

        btnPublicar.setEnabled(false);
        btnPublicar.setText("A publicar...");

        AnuncioRepository repository = new AnuncioRepository(requireContext());
        repository.postarAnuncio(request, new AnuncioRepository.RepoCallback<Anuncio>() {
            @Override
            public void onSuccess(Anuncio result) {
                btnPublicar.setEnabled(true);
                btnPublicar.setText("Publicar Anúncio");

                Toast.makeText(requireContext(),
                        "✅ Anúncio publicado com sucesso! ID: " + result.getIdAnuncio(),
                        Toast.LENGTH_LONG).show();

                limparCampos();
            }

            @Override
            public void onError(String message) {
                btnPublicar.setEnabled(true);
                btnPublicar.setText("Publicar Anúncio");

                Toast.makeText(requireContext(),
                        "❌ Falha ao publicar: " + message,
                        Toast.LENGTH_LONG).show();

                System.err.println("❌ Erro ao publicar anúncio: " + message);
            }
        });
    }

    // ============================================================
    // 6. LIMPAR CAMPOS
    // ============================================================

    private void limparCampos() {
        inputTitulo.setText("");
        inputMensagem.setText("");
        inputRestricoes.setText("");
        spinnerLocais.setSelection(0);
        radioGroupPolitica.clearCheck();
    }
}