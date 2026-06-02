package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerAnuncioFragment extends Fragment {
    private static final String ARG_ANUNCIO = "anuncio";
    private ImageView btnRemover;
    private ProgressBar progressBar;
    private TextView txtAutor;
    private TextView txtLocal;
    private TextView txtMensagem;
    private TextView txtTempo;
    private TextView txtTitulo;

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
        this.txtTitulo = view.findViewById(R.id.txtTitulo);
        this.txtAutor = view.findViewById(R.id.txtAutor);
        this.txtLocal = view.findViewById(R.id.txtLocal);
        this.txtMensagem = view.findViewById(R.id.txtMensagem);
        this.txtTempo = view.findViewById(R.id.txtTempo);
        this.btnRemover = view.findViewById(R.id.btnRemover);
        this.progressBar = view.findViewById(R.id.progressVerAnuncio);
        
        view.findViewById(R.id.btnBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        Anuncio anuncio = null;
        if (getArguments() != null) {
            anuncio = (Anuncio) getArguments().getSerializable(ARG_ANUNCIO);
        }
        if (anuncio != null) {
            preencherDados(anuncio);
            configurarRemocao(anuncio);
        }
        return view;
    }

    private void configurarRemocao(final Anuncio anuncio) {
        String emailLogado = KerberosAuthManager.getEmail(requireContext());
        if (emailLogado != null && emailLogado.equals(anuncio.getAutor())) {
            this.btnRemover.setVisibility(View.VISIBLE);
            this.btnRemover.setOnClickListener(v -> removerAnuncio(anuncio));
        } else {
            this.btnRemover.setVisibility(View.GONE);
        }
    }

    private void removerAnuncio(Anuncio anuncio) {
        this.btnRemover.setEnabled(false);
        if (this.progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        RetrofitClient.getInstance().getApi().removerAnuncio(anuncio.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Anúncio removido", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    btnRemover.setEnabled(true);
                    Toast.makeText(requireContext(), "Erro ao remover anúncio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnRemover.setEnabled(true);
                Toast.makeText(requireContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherDados(Anuncio anuncio) {
        this.txtTitulo.setText(valor(anuncio.getTitulo(), "Sem titulo"));
        this.txtAutor.setText(valor(anuncio.getAutor(), "Autor"));
        this.txtLocal.setText(valor(anuncio.getNome_local(), "Local"));
        this.txtMensagem.setText(valor(anuncio.getMensagem(), ""));
        this.txtTempo.setText(anuncio.getData_publicacao() != null ? "Publicado em " + anuncio.getData_publicacao() : "");
    }

    private String valor(String texto, String padrao) {
        return (texto == null || texto.trim().isEmpty()) ? padrao : texto;
    }
}
