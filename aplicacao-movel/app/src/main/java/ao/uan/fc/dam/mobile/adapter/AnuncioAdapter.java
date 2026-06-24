package ao.uan.fc.dam.mobile.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDateTime;
import java.util.List;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.InicioViewHolder> {
    private final List<Anuncio> anuncioList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Anuncio anuncio);
    }

    public AnuncioAdapter(List<Anuncio> anuncioList, OnItemClickListener listener) {
        this.anuncioList = anuncioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inicio_fragment, parent, false);
        return new InicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InicioViewHolder holder, int position) {
        Anuncio anuncio = anuncioList.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        
        String autorStr = (anuncio.getAutor() != null && anuncio.getAutor().getNome() != null) 
                ? anuncio.getAutor().getNome() : "Anônimo";
        holder.autorELocal.setText(autorStr + " · " + anuncio.getNome_local());
        
        // Modo de entrega determina ícone e cor
        if ("DESCENTRALIZADO".equalsIgnoreCase(anuncio.getModo_entrega())) {
            holder.imgTipoEntrega.setImageResource(R.drawable.wifi_icon);
            holder.iconContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
            holder.imgTipoEntrega.setImageTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
        } else {
            holder.imgTipoEntrega.setImageResource(R.drawable.location_on_icon);
            holder.iconContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
            holder.imgTipoEntrega.setImageTintList(ColorStateList.valueOf(Color.parseColor("#E67E22")));
        }

        holder.tempo.setText(formatTimeAgo(anuncio.getDataPublicacao()));
        
        // Exibição de pontos (F6)
        int pts = anuncio.getPontos() > 0 ? anuncio.getPontos() : 2; // Default 2 para demonstração
        holder.pontos.setText("+ " + pts + " pts");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(anuncio);
        });
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "N/D";
        try {
            long diff = java.time.Duration.between(dateTime, java.time.LocalDateTime.now()).getSeconds();
            if (diff < 0) diff = 0;
            if (diff < 60) return "Agora";
            if (diff < 3600) return "Há " + (diff / 60) + " min";
            if (diff < 86400) return "Há " + (diff / 3600) + " h";
            return "Há " + (diff / 86400) + " dias";
        } catch (Exception e) {
            return "Recente";
        }
    }

    @Override
    public int getItemCount() { return anuncioList.size(); }

    public void atualizar(List<Anuncio> novos) {
        anuncioList.clear();
        if (novos != null) anuncioList.addAll(novos);
        notifyDataSetChanged();
    }

    public static class InicioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, autorELocal, tempo, pontos;
        ImageView imgTipoEntrega;
        View iconContainer;

        public InicioViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textView42);
            autorELocal = itemView.findViewById(R.id.txtAutorELocal);
            tempo = itemView.findViewById(R.id.textView44);
            pontos = itemView.findViewById(R.id.textView45);
            imgTipoEntrega = itemView.findViewById(R.id.imgTipoEntrega);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}
