package ao.uan.fc.dam.mobile.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ao.uan.fc.dam.mobile.R;

public class InicioFragment extends Fragment {
    RecyclerView recyclerView;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recyclerAnuncios);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        return view;


    }

}