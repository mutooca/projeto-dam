package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Historico;
import java.util.List;


public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder> {
    private final List<Historico> historicoList;

    public PerfilAdapter(List<Historico> historicoList) {
        this.historicoList = historicoList;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public PerfilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemPerfil = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfil_fragment, parent, false);
        return new PerfilViewHolder(itemPerfil);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(PerfilViewHolder holder, int position) {
        Historico historico = this.historicoList.get(position);
        holder.tipo.setText(historico.getTipo() == null ? "Historico" : historico.getTipo());
        holder.nome.setText(historico.getNome() == null ? "" : historico.getNome());
        holder.dia.setText(historico.getDia() != null ? historico.getDia().toString() : "");
        holder.ponto.setText("+" + historico.getPontos() + " pts");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.historicoList.size();
    }

    public void atualizar(List<Historico> novosHistoricos) {
        this.historicoList.clear();
        if (novosHistoricos != null) {
            this.historicoList.addAll(novosHistoricos);
        }
        notifyDataSetChanged();
    }

    public static class PerfilViewHolder extends RecyclerView.ViewHolder {
        TextView dia;
        TextView nome;
        TextView ponto;
        TextView tipo;

        public PerfilViewHolder(View itemView) {
            super(itemView);
            this.tipo = (TextView) itemView.findViewById(R.id.txtHistoricoTipo);
            this.ponto = (TextView) itemView.findViewById(R.id.txtHistoricoPontos);
            this.dia = (TextView) itemView.findViewById(R.id.txtHistoricoDia);
            this.nome = (TextView) itemView.findViewById(R.id.txtHistoricoNome);
        }
    }
}