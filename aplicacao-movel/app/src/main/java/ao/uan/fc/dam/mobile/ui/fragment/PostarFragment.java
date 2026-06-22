package ao.uan.fc.dam.mobile.ui.fragment;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;
import ao.uan.fc.dam.mobile.ui.viewmodel.LocaisViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostarFragment extends Fragment {
    private final List<Local> locais = new ArrayList<>();
    private Spinner spinnerLocais;
    private EditText inputTitulo, inputMensagem, inputRestricoes, inputDuracao;
    private RadioGroup radioGroupPolitica, radioGroupEntrega;
    private Button btnPublicar;
    private AnunciosViewModel anunciosViewModel;
    private LocaisViewModel locaisViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postar, container, false);
        anunciosViewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
        locaisViewModel = new ViewModelProvider(this).get(LocaisViewModel.class);

        spinnerLocais = view.findViewById(R.id.spinnerLocais);
        inputTitulo = view.findViewById(R.id.inputTitulo);
        inputMensagem = view.findViewById(R.id.inputMensagem);
        inputRestricoes = view.findViewById(R.id.inputRestricoes);
        // inputDuracao = view.findViewById(R.id.inputDuracao); // Adicionado via código se não houver no XML
        radioGroupPolitica = view.findViewById(R.id.radioGroupPolitica);
        radioGroupEntrega = view.findViewById(R.id.radioGroupEntrega);
        btnPublicar = view.findViewById(R.id.btnPublicar);

        btnPublicar.setOnClickListener(v -> publicar());
        
        // F6: Ver chaves públicas para ajudar na restrição (Requisito 2.1.2)
        view.findViewById(R.id.inputRestricoes).setOnClickListener(v -> mostrarChavesPublicas());

        locaisViewModel.getLocales().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) {
                locais.clear();
                locais.addAll(lista);
                List<String> nomes = new ArrayList<>();
                for (Local l : lista) nomes.add(l.getNome());
                spinnerLocais.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, nomes));
            }
        });

        return view;
    }

    private void mostrarChavesPublicas() {
        RetrofitClient.getInstance().getApi().listarChavesPerfil().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String[] chaves = response.body().toArray(new String[0]);
                    new AlertDialog.Builder(requireContext()).setTitle("Chaves Disponíveis").setItems(chaves, (d, w) -> {
                        inputRestricoes.setText(chaves[w] + "=");
                        inputRestricoes.requestFocus();
                    }).show();
                }
            }
            @Override public void onFailure(Call<List<String>> call, Throwable t) {}
        });
    }

    private void publicar() {
        int pos = spinnerLocais.getSelectedItemPosition();
        if (pos < 0 || inputTitulo.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        String politica = radioGroupPolitica.getCheckedRadioButtonId() == R.id.radioWhitelist ? "WHITELIST" : "BLACKLIST";
        String modoEntrega = radioGroupEntrega.getCheckedRadioButtonId() == R.id.radioCentralizado ? "CENTRALIZADO" : "DESCENTRALIZADO";
        
        anunciosViewModel.postar(
            locais.get(pos).getIdLocal().toString(),
            inputTitulo.getText().toString(),
            inputMensagem.getText().toString(),
            politica,
            inputRestricoes.getText().toString(),
            modoEntrega
        );
        
        Toast.makeText(requireContext(), "Anúncio publicado!", Toast.LENGTH_SHORT).show();
    }
}
