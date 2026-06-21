package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Historico;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private List<Historico> historicoList;

    public PerfilAdapter(List<Historico> historicoList) {
        this.historicoList = historicoList;
    }

    @NonNull
    @Override
    public PerfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_perfil = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_perfil, parent, false);
        return new PerfilViewHolder(item_perfil);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilViewHolder holder, int position) {
        Historico historico = historicoList.get(position);
        holder.tipo.setText(historico.getTipo());
        holder.nome.setText(historico.getNome());
        holder.dia.setText(String.valueOf(historico.getDia()));
        holder.ponto.setText(String.valueOf(historico.getPontos()));

    }

    @Override
    public int getItemCount() {
        return historicoList.size();
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        TextView tipo;
        TextView ponto;
        TextView dia;
        TextView nome;

        public PerfilViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.textView38);
            ponto = itemView.findViewById(R.id.textView);
            dia = itemView.findViewById(R.id.textView40);
            nome = itemView.findViewById(R.id.textView39);
        }

        public TextView getTipo() {
            return tipo;
        }

        public void setTipo(TextView tipo) {
            this.tipo = tipo;
        }

        public TextView getPonto() {
            return ponto;
        }

        public void setPonto(TextView ponto) {
            this.ponto = ponto;
        }

        public TextView getDia() {
            return dia;
        }

        public void setDia(TextView dia) {
            this.dia = dia;
        }

        public TextView getNome() {
            return nome;
        }

        public void setNome(TextView nome) {
            this.nome = nome;
        }
    }
}
