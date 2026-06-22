package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;

public class VerAnuncioFragment extends Fragment {
    private static final String ARG_ANUNCIO = "anuncio";
    private AnunciosViewModel viewModel;

    public static VerAnuncioFragment novaInstancia(Anuncio anuncio) {
        VerAnuncioFragment fragment = new VerAnuncioFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ANUNCIO, anuncio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_anuncio, container, false);
        viewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);
        
        TextView txtTitulo = view.findViewById(R.id.txtTitulo);
        TextView txtAutor = view.findViewById(R.id.txtAutor);
        TextView txtTempo = view.findViewById(R.id.txtTempo);
        TextView txtMensagem = view.findViewById(R.id.txtMensagem);
        Button btnDelete = new Button(requireContext()); // Simulação de botão de remoção se não houver no XML
        btnDelete.setText("Remover Anúncio");
        
        if (getArguments() != null) {
            Anuncio anuncio = (Anuncio) getArguments().getSerializable(ARG_ANUNCIO);
            if (anuncio != null) {
                txtTitulo.setText(anuncio.getTitulo());
                txtAutor.setText("Por: " + (anuncio.getAutor() != null ? anuncio.getAutor().getNome() : anuncio.getAutorEmail()));
                txtTempo.setText("Publicado em: " + anuncio.getDataPublicacao());
                txtMensagem.setText(anuncio.getConteudo());

                // F4: Remover anúncio (Apenas se for o autor)
                String loggedEmail = SessionManager.getEmail(requireContext());
                if (loggedEmail != null && loggedEmail.equals(anuncio.getAutorEmail())) {
                    ((ViewGroup)view).addView(btnDelete);
                    btnDelete.setOnClickListener(v -> {
                        viewModel.remover(anuncio.getIdAnuncio());
                        getParentFragmentManager().popBackStack();
                        Toast.makeText(requireContext(), "Removendo...", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        return view;
    }
}
