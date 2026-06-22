package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.nome_local.setText(anuncio.getNome_local());
        holder.pontos.setText("0 pts"); // Backend não retornou pontos
        holder.tempo.setText(anuncio.getDataPublicacao() != null ? anuncio.getDataPublicacao().toString() : "");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(anuncio);
        });
    }

    @Override
    public int getItemCount() { return anuncioList.size(); }

    public void atualizar(List<Anuncio> novos) {
        anuncioList.clear();
        if (novos != null) anuncioList.addAll(novos);
        notifyDataSetChanged();
    }

    public static class InicioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, nome_local, tempo, pontos;
        public InicioViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textView42);
            nome_local = itemView.findViewById(R.id.textView41);
            tempo = itemView.findViewById(R.id.textView44);
            pontos = itemView.findViewById(R.id.textView45);
        }
    }
}
