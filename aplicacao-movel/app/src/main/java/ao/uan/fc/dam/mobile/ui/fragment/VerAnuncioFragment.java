package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;


public class VerAnuncioFragment extends Fragment {
    private TextView txtTituloDetalhe;
    private TextView txtMensagemDetalhe;
    private TextView txtLocalDetalhe;
    private TextView txtAutorNome;
    private FloatingActionButton btnDelete;
    private FloatingActionButton btnClose;
    private AnuncioRepository repository;
    private int idAnuncio;
    private Anuncio anuncioAtual;


    public VerAnuncioFragment(){
        super(R.layout.fragment_ver_anuncio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        txtTituloDetalhe = view.findViewById(R.id.txtTituloDetalhe);
        txtMensagemDetalhe = view.findViewById(R.id.txtMensagemDetalhe);
        txtLocalDetalhe = view.findViewById(R.id.txtLocalDetalhe);
        txtAutorNome = view.findViewById(R.id.txtAutorNome);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnClose = view.findViewById(R.id.btnClose);
        repository = new AnuncioRepository(requireContext());

        Bundle bundle = getArguments();

        if(bundle != null){
            idAnuncio = bundle.getInt("id_anuncio");
            carregarAnuncio();
        }

        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        btnDelete.setOnClickListener(v -> removerAnuncio());
    }

    private void carregarAnuncio(){
        repository.buscarPorId(idAnuncio, anuncioCompleto -> {

            if(anuncioCompleto == null)
                return;
            anuncioAtual = anuncioCompleto.getAnuncio();

            requireActivity().runOnUiThread(() -> {
                txtTituloDetalhe.setText(anuncioCompleto.getAnuncio().getTitulo());
                txtMensagemDetalhe.setText(anuncioCompleto.getAnuncio().getConteudo());

                if(anuncioCompleto.getLocal() != null){
                    txtLocalDetalhe.setText(anuncioCompleto.getLocal().getNome());
                }

                if(anuncioCompleto.getAutor() != null){
                    txtAutorNome.setText(anuncioCompleto.getAutor().getNome());
                }
            });
        });
    }

    private void removerAnuncio(){
        if(anuncioAtual == null)
            return;

        repository.remover(anuncioAtual, resultado -> {
                    requireActivity().runOnUiThread(() -> {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    });
        });
    }
}