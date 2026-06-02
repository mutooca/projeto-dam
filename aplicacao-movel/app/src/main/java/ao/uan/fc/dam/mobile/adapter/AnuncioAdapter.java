package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;
import java.util.List;

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
        View itemInicio = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inicio_fragment, parent, false);
        return new InicioViewHolder(itemInicio);
    }

    @Override
    public void onBindViewHolder(@NonNull InicioViewHolder holder, int position) {
        final Anuncio anuncio = this.anuncioList.get(position);
        holder.titulo.setText(valor(anuncio.getTitulo(), "Sem titulo"));
        holder.nomeLocal.setText(valor(anuncio.getAutor(), "Autor") + " - " + valor(anuncio.getNome_local(), "Local"));
        holder.pontosPublicacao.setText("+" + anuncio.getPontos() + " pts");
        holder.tempoPublicacao.setText(anuncio.getData_publicacao() == null ? "" : anuncio.getData_publicacao().toString());
        holder.itemView.setOnClickListener(v -> {
            if (this.listener != null) {
                this.listener.onItemClick(anuncio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.anuncioList.size();
    }

    public void atualizar(List<Anuncio> novosAnuncios) {
        this.anuncioList.clear();
        if (novosAnuncios != null) {
            this.anuncioList.addAll(novosAnuncios);
        }
        notifyDataSetChanged();
    }

    private String valor(String texto, String padrao) {
        return (texto == null || texto.trim().isEmpty()) ? padrao : texto;
    }

    public static class InicioViewHolder extends RecyclerView.ViewHolder {
        TextView nomeLocal;
        TextView pontosPublicacao;
        TextView tempoPublicacao;
        TextView titulo;

        public InicioViewHolder(View itemView) {
            super(itemView);
            this.titulo = itemView.findViewById(R.id.textView42);
            this.nomeLocal = itemView.findViewById(R.id.textView41);
            this.tempoPublicacao = itemView.findViewById(R.id.textView44);
            this.pontosPublicacao = itemView.findViewById(R.id.textView45);
        }
    }
}
