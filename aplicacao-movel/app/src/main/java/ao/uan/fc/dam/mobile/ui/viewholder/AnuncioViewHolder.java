package ao.uan.fc.dam.mobile.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;
import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;

public class AnuncioViewHolder extends RecyclerView.ViewHolder {
    private final TextView titulo;
    private final TextView autorLocal;
    private final TextView tempo;
    private final ImageView tipoEntrega;

    public AnuncioViewHolder(View itemView){
        super(itemView);

        titulo = itemView.findViewById(R.id.textView42);
        autorLocal = itemView.findViewById(R.id.txtAutorELocal);
        tempo = itemView.findViewById(R.id.textView44);
        tipoEntrega = itemView.findViewById(R.id.imgTipoEntrega);

    }

    public void bind(AnuncioModel anuncio)
    {
        titulo.setText(anuncio.getTitulo());
        autorLocal.setText(
                anuncio.getAutor()
                        + " • "
                        + anuncio.getLocal()
        );
        tempo.setText("Agora");
        if (anuncio.getModoEntrega()
                .equals("DESCENTRALIZADO")) {
            tipoEntrega.setImageResource(
                    R.drawable.wifi_icon
            );
        } else {
            tipoEntrega.setImageResource(
                    R.drawable.location_on_icon
            );
        }
    }
}