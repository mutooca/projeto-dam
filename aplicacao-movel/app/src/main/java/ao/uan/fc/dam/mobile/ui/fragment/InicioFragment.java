package ao.uan.fc.dam.mobile.ui.fragment;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.ui.adapter.AnuncioAdapter;
import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;


public class InicioFragment extends Fragment {
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
        
        setupObservers();
        carregarNumeroLocais();

        adapter.setOnAnuncioClickListener(new AnuncioAdapter.OnAnuncioClickListener() {
            @Override
            public void onClick(AnuncioModel anuncio) {
                Fragment fragment;
                Bundle bundle = new Bundle();

                if ("DESCENTRALIZADO".equals(anuncio.getModoEntrega())) {
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
        var liveDataAnuncios = anuncioRepository.listarTodosLiveData();
        var liveDataRecebidos = anuncioRecebidoRepository.listarTodosLiveData();

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
                        "DESCENTRALIZADO"
                ));
            }
        }

        anunciosCombined.setValue(modelos);
    }

    @Override
    public void onResume() {
        super.onResume();
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
