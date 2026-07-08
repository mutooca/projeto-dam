package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;

/**
 * Arquiteto: Visualização Detalhada de Anúncio
 * Alinhado com a especificação 2.1.3 e 2.1.4.
 */
public class CaixaFragment extends Fragment {
    private static final String ARG_ANUNCIO = "anuncio_detalhe";
    private Anuncio anuncio;
    private AnunciosViewModel viewModel;

    public static CaixaFragment novaInstancia(Anuncio anuncio) {
        CaixaFragment fragment = new CaixaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ANUNCIO, anuncio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caixa, container, false);
        viewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);

        TextView txtTitulo = view.findViewById(R.id.txtTituloDetalhe);
        TextView txtLocal = view.findViewById(R.id.txtLocalDetalhe);
        TextView txtMensagem = view.findViewById(R.id.txtMensagemDetalhe);
        TextView txtAutor = view.findViewById(R.id.txtAutorNome);

        if (getArguments() != null) {
            anuncio = (Anuncio) getArguments().getSerializable(ARG_ANUNCIO);
            if (anuncio != null) {
                txtTitulo.setText(anuncio.getTitulo());
                txtLocal.setText(anuncio.getNome_local() != null ? anuncio.getNome_local() : "Global");
                txtMensagem.setText(anuncio.getConteudo());

                String autorNome = (anuncio.getAutor() != null && anuncio.getAutor().getNome() != null)
                        ? anuncio.getAutor().getNome() : anuncio.getAutorEmail();
                txtAutor.setText(autorNome != null ? autorNome : "Anônimo");
            }
        }

        view.findViewById(R.id.btnClose).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // ⭐ BOTÃO ELIMINAR ⭐
        view.findViewById(R.id.btnDelete).setOnClickListener(v -> confirmarExclusao());

        return view;
    }

    private void confirmarExclusao() {
        if (anuncio == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Anúncio")
                .setMessage("Tem certeza que deseja eliminar este anúncio permanentemente?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // ⭐ USAR O REPOSITÓRIO DIRETAMENTE ⭐
                    AnuncioRepository repository = new AnuncioRepository(requireContext());
                    String email = ao.uan.fc.dam.mobile.security.SessionManager.getEmail(requireContext());

                    if (email == null) {
                        Toast.makeText(getContext(), "Sessão inválida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    repository.removerAnuncio(anuncio.getIdAnuncio(), email, new AnuncioRepository.RepoCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "✅ Anúncio removido com sucesso", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), "❌ Erro ao remover: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}