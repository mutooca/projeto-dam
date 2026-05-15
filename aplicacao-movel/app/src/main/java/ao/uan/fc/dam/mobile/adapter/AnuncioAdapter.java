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

    private List<Anuncio> anuncioList;

    public AnuncioAdapter(List<Anuncio> anuncioList) {
        this.anuncioList = anuncioList;
    }

    @NonNull
    @Override
    public InicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_inicio = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inicio_fragment, parent, false);
        return new InicioViewHolder(item_inicio);
    }

    @Override
    public void onBindViewHolder(@NonNull InicioViewHolder holder, int position) {
        Anuncio anuncio = anuncioList.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.nome_local.setText(anuncio.getNome_local());
        holder.pontos_publicacao.setText(String.valueOf(anuncio.getPontos()));
        holder.tempo_publicacao.setText(String.valueOf(anuncio.getData_publicacao()));

    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    public static class InicioViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        TextView nome_local;
        TextView tempo_publicacao;
        TextView pontos_publicacao;

        public InicioViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textView42);
            nome_local = itemView.findViewById(R.id.textView41);
            tempo_publicacao = itemView.findViewById(R.id.textView44);
            pontos_publicacao = itemView.findViewById(R.id.textView45);
        }

        public TextView getTitulo() {
            return titulo;
        }

        public void setTitulo(TextView titulo) {
            this.titulo = titulo;
        }

        public TextView getNome_local() {
            return nome_local;
        }

        public void setNome_local(TextView nome_local) {
            this.nome_local = nome_local;
        }

        public TextView getTempo_publicacao() {
            return tempo_publicacao;
        }

        public void setTempo_publicacao(TextView tempo_publicacao) {
            this.tempo_publicacao = tempo_publicacao;
        }

        public TextView getPontos_publicacao() {
            return pontos_publicacao;
        }

        public void setPontos_publicacao(TextView pontos_publicacao) {
            this.pontos_publicacao = pontos_publicacao;
        }
    }
}
