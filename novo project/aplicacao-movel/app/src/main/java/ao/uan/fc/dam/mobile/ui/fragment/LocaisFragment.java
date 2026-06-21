package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.adapter.LocalAdapter;

public class LocaisFragment extends Fragment {
    private List<Local> locais;
    private LocalAdapter adapter;
    private RecyclerView localRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_locais, container, false);
        locais = geradorLocal();
        localRecyclerView = view.findViewById(R.id.recyclerViewLocais);
        localRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        localRecyclerView.setHasFixedSize(true);
        adapter = new LocalAdapter(locais);

        return view;
    }

    private List<Local> geradorLocal(){
        List<Local> localList = new ArrayList<>();
        localList.add(new Local());
        localList.add(new Local());
        localList.add(new Local());
        localList.add(new Local());

        return localList;
    }
}