package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        if (anuncioAtual.getIdServidor() != null && !anuncioAtual.getIdServidor().isBlank()) {
            Log.d("VerAnuncio", "A eliminar anuncio remoto idServidor=" + anuncioAtual.getIdServidor());
            repository.eliminarRemoto(
                    anuncioAtual,
                    resultado -> requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Anúncio eliminado com sucesso.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }),
                    erro -> requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
            );
            return;
        }

        // Anuncio criado antes desta funcionalidade existir: sem idServidor nao ha como eliminar
        // remotamente. Remove-se apenas o registo local para nao bloquear o utilizador.
        Log.w("VerAnuncio", "Anuncio sem idServidor (id_anuncio=" + anuncioAtual.getIdAnuncio()
                + "). A remover apenas localmente.");
        Toast.makeText(requireContext(),
                "Este anúncio não tem ID remoto (foi criado antes desta funcionalidade). A remover apenas localmente.",
                Toast.LENGTH_LONG).show();
        repository.remover(anuncioAtual, resultado -> {
                    requireActivity().runOnUiThread(() -> {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    });
        });
    }
}