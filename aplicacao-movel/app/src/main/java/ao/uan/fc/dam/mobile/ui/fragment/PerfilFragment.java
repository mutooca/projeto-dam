package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.PerfilAdapter;
import ao.uan.fc.dam.mobile.model.Historico;

public class PerfilFragment extends Fragment {

    private List<Historico> historicos;
    private RecyclerView perfilRecyclerView;
    private PerfilAdapter adapter;

    public PerfilFragment() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar RecyclerView
        perfilRecyclerView = view.findViewById(R.id.recyclerViewPerfil);

        // Gerar dados
        historicos = geradorHistorico();

        // Configurar Adapter
        adapter = new PerfilAdapter(historicos);

        // Configurar RecyclerView
        perfilRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        perfilRecyclerView.setAdapter(adapter);

        return view;
    }

    public List<Historico> geradorHistorico() {

        List<Historico> historicoList = new ArrayList<>();

        historicoList.add(new Historico(
                "Entrega concluída",
                10,
                "Luanda",
                LocalDateTime.now()
        ));

        historicoList.add(new Historico(
                "Nova publicação",
                5,
                "Benfica",
                LocalDateTime.now()
        ));

        historicoList.add(new Historico(
                "Anúncio removido",
                2,
                "Talatona",
                LocalDateTime.now()
        ));

        historicoList.add(new Historico(
                "Entrega pendente",
                8,
                "Kilamba",
                LocalDateTime.now()
        ));

        return historicoList;
    }
}

