package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Local;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {
    private List<Local> localList;

    public LocalAdapter(List<Local> localList) {
        this.localList = localList;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_local = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_locais, parent, false);
        return new LocalViewHolder(item_local);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.nome_local.setText(local.getNome());
        holder.coordenada.setText(local.getCoordenada_gps());
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }


    public static class LocalViewHolder extends RecyclerView.ViewHolder{
        private TextView nome_local;
        private TextView coordenada;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_local = itemView.findViewById(R.id.textView37);
            coordenada = itemView.findViewById(R.id.textView27);
        }

        public TextView getNome_local() {
            return nome_local;
        }

        public void setNome_local(TextView nome_local) {
            this.nome_local = nome_local;
        }

        public TextView getCoordenada() {
            return coordenada;
        }

        public void setCoordenada(TextView coordenada) {
            this.coordenada = coordenada;
        }
    }
}
