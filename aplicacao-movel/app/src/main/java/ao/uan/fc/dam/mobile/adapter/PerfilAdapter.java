package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private List<Anuncio> anuncioList = new ArrayList<>();

    public PerfilAdapter(List<Anuncio> anuncios) {
        if (anuncios != null) {
            this.anuncioList = anuncios;
        }
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_fragment, parent, false);
        return new PerfilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        Anuncio anuncio = anuncioList.get(position);
        
        // 1. Título e Local (UX: Título - Local)
        String localName = anuncio.getNome_local() != null ? anuncio.getNome_local() : "Local desconhecido";
        holder.txtTitleLocal.setText(anuncio.getTitulo() + " - " + localName);
        
        // 2. Tempo Relativo
        holder.txtRelativeTime.setText(getRelativeTime(anuncio.getDataPublicacao()));
        
        // 3. Ícone baseado no Modo de Entrega (F2.1.4)
        if ("DESCENTRALIZADO".equalsIgnoreCase(anuncio.getModo_entrega())) {
            holder.imgMode.setImageResource(R.drawable.notifications_icon); // WiFi/P2P Icon
        } else {
            holder.imgMode.setImageResource(R.drawable.trending_up_icon); // Centralizado Icon
        }
        
        // 4. Pontos
        holder.txtPoints.setText("+" + anuncio.getPontos());
    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    public void atualizar(List<Anuncio> novos) {
        this.anuncioList = (novos != null) ? novos : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String getRelativeTime(LocalDateTime date) {
        if (date == null) return "Recentemente";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(date, now);
        
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Agora";
        if (seconds < 3600) return "Há " + (seconds / 60) + " min";
        if (seconds < 86400) {
            if (date.getDayOfMonth() == now.getDayOfMonth()) return "Hoje";
            return "Ontem";
        }
        if (seconds < 172800) return "Ontem";
        return "Há " + (seconds / 86400) + " dias";
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitleLocal, txtRelativeTime, txtPoints;
        ImageView imgMode;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitleLocal = itemView.findViewById(R.id.txtTitleLocal);
            txtRelativeTime = itemView.findViewById(R.id.txtRelativeTime);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            imgMode = itemView.findViewById(R.id.imgMode);
        }
    }
}
