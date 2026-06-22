package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Local;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {
    private final List<Local> localList;
    private final OnLocalClickListener listener;

    public interface OnLocalClickListener {
        void onLocalClick(Local local);
        void onLocalDelete(Local local);
    }

    public LocalAdapter(List<Local> localList, OnLocalClickListener listener) {
        this.localList = localList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_locais_fragment, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.nome.setText(local.getNome() != null ? local.getNome() : "Local sem nome");
        
        if (local.isWifi()) {
            holder.coordenada.setText("Modo: WiFi (ID: " + local.getSsidWifi() + ")");
            holder.icone.setImageResource(R.drawable.inbox_icon); // Usando ícone de sinal como fallback
        } else {
            String gps = String.format("GPS: %.4f, %.4f (Raio: %dm)", 
                local.getLatitude(), local.getLongitude(), local.getRaio() != null ? local.getRaio() : 0);
            holder.coordenada.setText(gps);
            holder.icone.setImageResource(R.drawable.location_on_icon);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onLocalClick(local);
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLocalDelete(local);
            return true;
        });
    }

    @Override
    public int getItemCount() { return localList.size(); }

    public void atualizar(List<Local> novos) {
        localList.clear();
        if (novos != null) localList.addAll(novos);
        notifyDataSetChanged();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        TextView nome, coordenada;
        ImageView icone;
        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textView27);
            coordenada = itemView.findViewById(R.id.textView37);
            icone = itemView.findViewById(R.id.imageView4);
        }
    }
}
