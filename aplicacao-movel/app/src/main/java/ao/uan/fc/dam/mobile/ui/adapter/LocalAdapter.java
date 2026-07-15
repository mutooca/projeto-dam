package ao.uan.fc.dam.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;
import ao.uan.fc.dam.mobile.ui.viewholder.LocalViewHolder;


public class LocalAdapter extends RecyclerView.Adapter<LocalViewHolder>{

    private List<LocalCompleto> locais = new ArrayList<>();
    private OnLocalClickListener listener;

    public interface OnLocalClickListener{
        void onClick(Local local);
        void onLongClick(Local local);
    }

    public void setOnLocalClickListener(OnLocalClickListener listener){
        this.listener = listener;
    }

    public void setLocais(List<LocalCompleto> lista){
        locais = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_locais_fragment, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LocalViewHolder holder,
            int position){

        LocalCompleto localCompleto = locais.get(position);
        holder.bind(localCompleto);
        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onClick(localCompleto.local);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if(listener != null){
                listener.onLongClick(localCompleto.local);
            }
            return true;
        });
    }

    @Override
    public int getItemCount(){
        return locais.size();
    }
}