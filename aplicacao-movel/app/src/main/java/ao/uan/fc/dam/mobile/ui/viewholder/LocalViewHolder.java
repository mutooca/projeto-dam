package ao.uan.fc.dam.mobile.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.enums.TipoCoordenada;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;


public class LocalViewHolder extends RecyclerView.ViewHolder {
    private final TextView nome;
    private final TextView coordenadas;
    private final ImageView icone;

    public LocalViewHolder(View itemView) {
        super(itemView);
        nome = itemView.findViewById(
                R.id.txtLocalNome
        );

        coordenadas = itemView.findViewById(R.id.txtCoordenadas);
        icone = itemView.findViewById(
                R.id.imgLocalIcon
        );
    }

    public void bind(LocalCompleto localCompleto) {
        nome.setText(
                localCompleto.local.getNome()
        );

        if(localCompleto.coordenadaGps != null){
            String texto =
                    "Lat: "
                            + localCompleto.coordenadaGps.getLatitude()
                            + "\nLon: "
                            + localCompleto.coordenadaGps.getLongitude();
            coordenadas.setText(texto);
        }else{
            coordenadas.setText(
                    "Sem coordenadas"
            );
        }

        if(localCompleto.local.getTipoCoordenada()
                == TipoCoordenada.GPS){
            icone.setImageResource(
                    R.drawable.location_on_icon
            );
        }else{
            icone.setImageResource(
                    R.drawable.wifi_icon
            );
        }
    }
}