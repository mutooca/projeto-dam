package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Historico;
import ao.uan.fc.dam.mobile.adapter.PerfilAdapter;

public class PerfilFragment extends Fragment {
    private List<Historico> historicos;
    private RecyclerView perfilRecyclerView;
    private PerfilAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        return view;
    }

    public List<Historico> geradorHitorico(){
        List<Historico> historicoList = new ArrayList<>();
        historicoList.add(new Historico());
        historicoList.add(new Historico(new Historico()));
        historicoList.add(new Historico());
        historicoList.add(new Historico());
        return historicoList;
    }
}