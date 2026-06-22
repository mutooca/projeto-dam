package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;

/**
 * Arquiteto: Visualização Detalhada de Anúncio
 * Alinhado com a especificação 2.1.3 e 2.1.4.
 */
public class CaixaFragment extends Fragment {
    private static final String ARG_ANUNCIO = "anuncio_detalhe";

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
        
        TextView txtTitulo = view.findViewById(R.id.textView18);
        TextView txtLocal = view.findViewById(R.id.textView19);
        TextView txtMensagem = view.findViewById(R.id.textView20);
        TextView txtAutor = view.findViewById(R.id.textView21);
        ImageView imgAnuncio = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            Anuncio anuncio = (Anuncio) getArguments().getSerializable(ARG_ANUNCIO);
            if (anuncio != null) {
                txtTitulo.setText(anuncio.getTitulo());
                txtLocal.setText("Local: " + (anuncio.getNome_local() != null ? anuncio.getNome_local() : "Global"));
                
                // Exibição de política no corpo da mensagem (Baseline visual)
                String infoPolitica = "\n\n--- Info Sistema ---\n" +
                        "Modo: " + (anuncio.getModo_entrega() != null ? anuncio.getModo_entrega() : "Centralizado") + "\n" +
                        "Política: " + (anuncio.getCategoria() != null ? anuncio.getCategoria() : "Padrão");
                
                txtMensagem.setText(anuncio.getConteudo() + infoPolitica);
                txtAutor.setText("Publicado por: " + (anuncio.getAutorEmail() != null ? anuncio.getAutorEmail() : "Anónimo"));

                imgAnuncio.setImageResource(R.drawable.anuncio_icon);
            }
        }

        return view;
    }
}
