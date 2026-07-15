package ao.uan.fc.dam.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.Historico;
import ao.uan.fc.dam.mobile.ui.viewholder.HistoricoViewHolder;

public class HistoricoAdapter
        extends RecyclerView.Adapter<HistoricoViewHolder>{

    private List<Historico> lista = new ArrayList<>();

    public void setHistorico(List<Historico> lista){

        this.lista = lista;

        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public HistoricoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ){

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_perfil_fragment,
                        parent,
                        false
                );

        return new HistoricoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoricoViewHolder holder,
            int position
    ){

        holder.bind(lista.get(position));

    }

    @Override
    public int getItemCount(){

        return lista.size();

    }

}