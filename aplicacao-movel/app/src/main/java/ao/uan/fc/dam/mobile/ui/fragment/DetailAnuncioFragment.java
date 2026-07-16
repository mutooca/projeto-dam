package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.format.DateTimeFormatter;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;

public class DetailAnuncioFragment extends Fragment {

    private TextView txtTituloDetalhe;
    private TextView txtModoEntrega;
    private TextView txtLocalDetalhe;
    private TextView txtDataDetalhe;
    private TextView txtMensagemDetalhe;
    private TextView txtPoliticaDetalhe;
    private TextView txtAutorNome;
    private FloatingActionButton btnClose;

    private AnuncioRecebidoRepository repository;
    private String msgId;

    public DetailAnuncioFragment() {
        super(R.layout.fragment_detail_anuncio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTituloDetalhe = view.findViewById(R.id.txtTituloDetalhe);
        txtModoEntrega = view.findViewById(R.id.txtModoEntrega);
        txtLocalDetalhe = view.findViewById(R.id.txtLocalDetalhe);
        txtDataDetalhe = view.findViewById(R.id.txtDataDetalhe);
        txtMensagemDetalhe = view.findViewById(R.id.txtMensagemDetalhe);
        txtPoliticaDetalhe = view.findViewById(R.id.txtPoliticaDetalhe);
        txtAutorNome = view.findViewById(R.id.txtAutorNome);
        btnClose = view.findViewById(R.id.btnClose);

        repository = new AnuncioRecebidoRepository(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            msgId = bundle.getString("msg_id");
            carregarDetalhes();
        }

        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void carregarDetalhes() {
        repository.buscarPorMsgId(msgId, anuncio -> {
            if (anuncio == null) return;

            requireActivity().runOnUiThread(() -> {
                txtTituloDetalhe.setText(anuncio.getTitulo());
                txtModoEntrega.setText(anuncio.getModoEntrega() != null ? anuncio.getModoEntrega() : "DESCENTRALIZADO");
                txtLocalDetalhe.setText(anuncio.getLocal());
                txtMensagemDetalhe.setText(anuncio.getConteudo());
                txtAutorNome.setText(anuncio.getAutor());

                if (anuncio.getDataRececao() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    txtDataDetalhe.setText("Recebido em: " + anuncio.getDataRececao().format(formatter));
                }

                String politica = "Tipo: " + anuncio.getPoliticaTipo() + " | Chaves: " + anuncio.getPoliticaChaves();
                txtPoliticaDetalhe.setText(politica);
            });
        });
    }
}
