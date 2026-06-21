package ao.uan.fc.dam.mobile.ui.fragment;

import static java.time.LocalDate.now;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.adapter.AnuncioAdapter;

public class InicioFragment extends Fragment {
    private List<Anuncio> anuncios;
    private AnuncioAdapter adapter;
    private RecyclerView inicioRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        anuncios = geradorAnuncio();
        inicioRecyclerView = view.findViewById(R.id.recyclerViewInicio);
        inicioRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        inicioRecyclerView.setHasFixedSize(true);
        adapter = new AnuncioAdapter(anuncios);
        inicioRecyclerView.setAdapter(adapter);

        return view;
    }

    private List<Anuncio> geradorAnuncio(){
        List<Anuncio> anuncioList = new ArrayList<>();
        anuncioList.add(new Anuncio(UUID.randomUUID(), "Arrenda-se uma casa", "Arrenda-se uma casa T3 com os seguintes compartimentos\n3 quartos, uma delas com suite\n uma sala vasta\n1 quintal\n1 WC\n1 cozinha\nContactos:924335100",
                LocalDate.now(), "lido", "1º de Maio", 4, 2, "Maria"));
        anuncioList.add(new Anuncio(UUID.randomUUID(), "Recrutamento", " Precisa-se de uma domestica\nContactos:952388771",
                LocalDate.now(), "lido", "1º de Maio", 4, 2, "Osana"));
        anuncioList.add(new Anuncio(UUID.randomUUID(), "Curso de Inglês", "Dá-se aulas de inglês ao domicilio\nContactos:924234100",
                LocalDate.now(), "lido", "1º de Maio", 4, 2, "Kama"));
        anuncioList.add(new Anuncio(UUID.randomUUID(), "Arrenda-se uma barbearia", "Arrenda-se uma barbeara nas imediações do catetão\nContactos:924335100",
                LocalDate.now(), "lido", "1º de Maio", 4, 2, "Victor"));

        return anuncioList;
    }
}