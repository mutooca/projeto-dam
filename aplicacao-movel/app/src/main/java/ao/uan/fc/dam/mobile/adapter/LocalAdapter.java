package ao.uan.fc.dam.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Local;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {
    private final List<Local> localList;
    private OnLocalClickListener listener;

    public interface OnLocalClickListener {
        void onLocalClick(Local local);
        void onLocalDelete(Local local);
    }

    public LocalAdapter(List<Local> localList) {
        this.localList = localList;
    }

    public void setOnLocalClickListener(OnLocalClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLocal = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locais_fragment, parent, false);
        return new LocalViewHolder(itemLocal);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.nomeLocal.setText(local.getNome() == null ? "Local sem nome" : local.getNome());

        String detalhe = "";
        if (local.getDistanciaKm() != null) {
            detalhe = String.format(Locale.getDefault(), "%.2f km", local.getDistanciaKm());
        } else if (local.getLatitude() != null && local.getLongitude() != null) {
            detalhe = local.getLatitude() + ", " + local.getLongitude();
        }
        holder.detalhe.setText(detalhe);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onLocalClick(local);
        });

        if (holder.btnDelete != null) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onLocalDelete(local);
            });
        }
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    public void atualizar(List<Local> novosLocais) {
        localList.clear();
        if (novosLocais != null) {
            localList.addAll(novosLocais);
        }
        notifyDataSetChanged();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeLocal;
        private final TextView detalhe;
        private final ImageView btnDelete;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeLocal = itemView.findViewById(R.id.txtNomeLocal);
            detalhe = itemView.findViewById(R.id.txtDetalheLocal);
            // Tentando encontrar um ícone de delete no layout
            btnDelete = itemView.findViewById(R.id.btnDeleteLocal);
        }
    }
}
