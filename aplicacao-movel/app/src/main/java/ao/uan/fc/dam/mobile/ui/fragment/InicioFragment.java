package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.AnuncioAdapter;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;
import ao.uan.fc.dam.mobile.ui.viewmodel.LocaisViewModel;
import ao.uan.fc.dam.mobile.ui.viewmodel.PerfilViewModel;

public class InicioFragment extends Fragment {
    private AnuncioAdapter adapter;
    private AnunciosViewModel viewModel;
    private LocaisViewModel locaisViewModel;
    private PerfilViewModel perfilViewModel;

    private TextView txtSaudacao, txtTotalAds, txtTotalLocais;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        viewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
        locaisViewModel = new ViewModelProvider(this).get(LocaisViewModel.class);
        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        txtSaudacao = view.findViewById(R.id.textView7);
        txtTotalAds = view.findViewById(R.id.textView29);
        txtTotalLocais = view.findViewById(R.id.textView31);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewInicio);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new AnuncioAdapter(new ArrayList<>(), this::abrirDetalhesAnuncio);
        recyclerView.setAdapter(adapter);

        setupObservers();

        // ⭐ CARREGAR DADOS INICIAIS ⭐
        String email = SessionManager.getEmail(requireContext());
        if (email != null) {
            viewModel.carregarMeusAnuncios(requireContext(), email);
        }

        return view;
    }

    private void setupObservers() {
        // Observa Perfil para saudação personalizada
        perfilViewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getNome() != null) {
                txtSaudacao.setText("Olá, " + user.getNome() + "!");
            }
        });

        // ⭐ OBSERVA ANÚNCIOS ⭐
        viewModel.getMeusAnuncios().observe(getViewLifecycleOwner(), anuncios -> {
            if (anuncios != null) {
                adapter.atualizar(anuncios);
                txtTotalAds.setText(String.valueOf(anuncios.size()));
            }
        });

        // Observa Locais para o contador
        locaisViewModel.getLocales().observe(getViewLifecycleOwner(), locais -> {
            if (locais != null) {
                txtTotalLocais.setText(String.valueOf(locais.size()));
            }
        });

        // Observa erros
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalhesAnuncio(Anuncio anuncio) {
        CaixaFragment fragment = CaixaFragment.novaInstancia(anuncio);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}