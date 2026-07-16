package ao.uan.fc.dam.mobile.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.ui.adapter.AnuncioAdapter;
import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;
import ao.uan.fc.dam.mobile.util.SessionManager;


public class InicioFragment extends Fragment {
    private static final String TAG = "InicioFragment";
    private static final int REQUEST_LOCATION = 101;

    private TextView bemVindoUser;
    private TextView numeroNotificacao;
    private EditText pesquisar;
    private TextView numeroAnuncio;
    private TextView numeroLocais;
    private AnuncioAdapter adapter;
    private AnuncioRepository anuncioRepository;
    private LocalRepository localRepository;
    private RecyclerView recyclerViewInicio;
    private AnuncioRecebidoRepository anuncioRecebidoRepository;
    private LocalizacaoProvider localizacaoProvider;
    private SessionManager sessionManager;

    private final MediatorLiveData<List<AnuncioModel>> anunciosCombined = new MediatorLiveData<>();


    public InicioFragment(){
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        bemVindoUser = view.findViewById(R.id.textView7);
        numeroNotificacao = view.findViewById(R.id.txtBadgeNotif);
        pesquisar = view.findViewById(R.id.searchView);
        numeroAnuncio = view.findViewById(R.id.textView29);
        numeroLocais = view.findViewById(R.id.textView31);
        recyclerViewInicio = view.findViewById(R.id.recyclerViewInicio);

        adapter = new AnuncioAdapter();

        recyclerViewInicio.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewInicio.setAdapter(adapter);
        anuncioRepository = new AnuncioRepository(requireContext());
        localRepository = new LocalRepository(requireContext());
        anuncioRecebidoRepository = new AnuncioRecebidoRepository(requireContext());
        localizacaoProvider = new LocalizacaoProvider(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupObservers();
        carregarNumeroLocais();
        sincronizarAnunciosPorLocalizacao();

        adapter.setOnAnuncioClickListener(new AnuncioAdapter.OnAnuncioClickListener() {
            @Override
            public void onClick(AnuncioModel anuncio) {
                Fragment fragment;
                Bundle bundle = new Bundle();

                boolean isRecebido = anuncio.getMsgId() != null;
                if (isRecebido) {
                    bundle.putString("msg_id", anuncio.getMsgId());
                    fragment = new DetailAnuncioFragment();
                } else {
                    bundle.putInt("id_anuncio", anuncio.getId());
                    fragment = new VerAnuncioFragment();
                }

                fragment.setArguments(bundle);
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onLongClick(AnuncioModel anuncio) {
            }
        });
    }

    private void setupObservers() {
        int idUtilizador = sessionManager.getIdUtilizador();
        var liveDataAnuncios = anuncioRepository.listarTodosLiveData(idUtilizador);
        var liveDataRecebidos = anuncioRecebidoRepository.listarTodosLiveData(idUtilizador);

        anunciosCombined.addSource(liveDataAnuncios, lista -> updateCombinedList(lista, liveDataRecebidos.getValue()));
        anunciosCombined.addSource(liveDataRecebidos, recebidos -> updateCombinedList(liveDataAnuncios.getValue(), recebidos));

        anunciosCombined.observe(getViewLifecycleOwner(), modelos -> {
            adapter.setAnuncios(modelos);
            numeroAnuncio.setText(String.valueOf(modelos.size()));
        });
    }

    private void updateCombinedList(List<AnuncioCompleto> lista, List<AnuncioRecebido> recebidos) {
        List<AnuncioModel> modelos = new ArrayList<>();

        if (lista != null) {
            for (var item : lista) {
                modelos.add(new AnuncioModel(
                        item.anuncio.getIdAnuncio(),
                        item.anuncio.getTitulo(),
                        item.autor != null ? item.autor.getNome() : "",
                        item.local != null ? item.local.getNome() : "",
                        item.anuncio.getConteudo(),
                        item.anuncio.getModoEntrega().name()
                ));
            }
        }

        if (recebidos != null) {
            for (AnuncioRecebido item : recebidos) {
                modelos.add(new AnuncioModel(
                        0,
                        item.getMsgId(),
                        item.getTitulo(),
                        item.getAutor(),
                        item.getLocal(),
                        item.getConteudo(),
                        item.getModoEntrega() != null ? item.getModoEntrega() : "DESCENTRALIZADO"
                ));
            }
        }

        anunciosCombined.setValue(modelos);
    }

    @Override
    public void onResume() {
        super.onResume();
        sincronizarAnunciosPorLocalizacao();
    }

    /**
     * Verifica junto do servidor se há anúncios centralizados elegíveis para este utilizador
     * na localização actual, respeitando a política whitelist/blacklist definida pelo autor
     * (o servidor decide com base no perfil real do utilizador, não confiamos em cache antiga).
     */
    private void sincronizarAnunciosPorLocalizacao() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION
            );
            return;
        }

        localizacaoProvider.obterLocalizacao((lat, lon) -> {
            if (Double.isNaN(lat) || Double.isNaN(lon)) {
                Log.w(TAG, "Localizacao indisponivel, a ignorar sincronizacao de anuncios.");
                return;
            }

            Log.d(TAG, "A verificar anuncios centralizados elegiveis em lat=" + lat + " lon=" + lon);
            anuncioRecebidoRepository.sincronizarPorLocalizacao(
                    lat,
                    lon,
                    novos -> Log.d(TAG, novos + " novo(s) anuncio(s) centralizado(s) recebido(s)"),
                    erro -> Log.w(TAG, "Erro ao sincronizar anuncios por localizacao: " + erro)
            );
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sincronizarAnunciosPorLocalizacao();
        }
    }

    private void carregarNumeroLocais(){
        localRepository.listarTodosComCoordenadas(lista ->{
            requireActivity().runOnUiThread(() ->{
                numeroLocais.setText(
                        String.valueOf(lista.size())
                );
            });
        });
    }
}
