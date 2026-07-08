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
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;

public class VerAnuncioFragment extends Fragment {
    private static final String ARG_ANUNCIO = "anuncio";
    private AnunciosViewModel viewModel;
    private Anuncio anuncio;

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

        if (getArguments() != null) {
            anuncio = (Anuncio) getArguments().getSerializable(ARG_ANUNCIO);
            if (anuncio != null) {
                txtTitulo.setText(anuncio.getTitulo());
                txtAutor.setText("Por: " + (anuncio.getAutor() != null ? anuncio.getAutor().getNome() : anuncio.getAutorEmail()));
                txtTempo.setText("Publicado em: " + anuncio.getDataPublicacao());
                txtMensagem.setText(anuncio.getConteudo());

                // ⭐ BOTÃO REMOVER (apenas se for o autor) ⭐
                String loggedEmail = SessionManager.getEmail(requireContext());
                if (loggedEmail != null && loggedEmail.equals(anuncio.getAutorEmail())) {
                    Button btnDelete = new Button(requireContext());
                    btnDelete.setText("Remover Anúncio");
                    btnDelete.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    getResources().getColor(android.R.color.holo_red_dark)
                            )
                    );
                    btnDelete.setTextColor(getResources().getColor(android.R.color.white));

                    // Adicionar botão ao layout
                    ViewGroup parent = (ViewGroup) view;
                    parent.addView(btnDelete);

                    btnDelete.setOnClickListener(v -> confirmarExclusao());
                }
            }
        }

        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        return view;
    }

    // ============================================================
    // ⭐ CONFIRMAR EXCLUSÃO ⭐
    // ============================================================
    private void confirmarExclusao() {
        if (anuncio == null) return;

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Anúncio")
                .setMessage("Tem certeza que deseja eliminar este anúncio permanentemente?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // ⭐ USAR REPOSITÓRIO DIRETAMENTE ⭐
                    String email = SessionManager.getEmail(requireContext());
                    if (email == null) {
                        Toast.makeText(requireContext(), "Sessão inválida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AnuncioRepository repository = new AnuncioRepository(requireContext());
                    repository.removerAnuncio(anuncio.getIdAnuncio(), email, new AnuncioRepository.RepoCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(requireContext(), "✅ Anúncio removido com sucesso", Toast.LENGTH_SHORT).show();
                            // Voltar para a tela anterior
                            getParentFragmentManager().popBackStack();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(requireContext(), "❌ Erro ao remover: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}