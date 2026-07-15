package ao.uan.fc.dam.mobile.ui.viewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;


public class AnuncioRecebidoViewHolder
        extends RecyclerView.ViewHolder {


    private TextView titulo;
    private TextView autorLocal;
    private TextView tempo;
    private ImageView tipoEntrega;


    public AnuncioRecebidoViewHolder(View itemView){

        super(itemView);

        titulo =
                itemView.findViewById(R.id.textView42);

        autorLocal =
                itemView.findViewById(R.id.txtAutorELocal);

        tempo =
                itemView.findViewById(R.id.textView44);

        tipoEntrega =
                itemView.findViewById(R.id.imgTipoEntrega);

    }



    public void bind(AnuncioRecebido anuncio){


        titulo.setText(
                anuncio.getTitulo()
        );


        autorLocal.setText(
                anuncio.getAutor()
                        +
                        " • "
                        +
                        anuncio.getLocal()
        );


        if(anuncio.getDataRececao()!=null){

            tempo.setText(
                    anuncio.getDataRececao().toString()
            );

        }


        tipoEntrega.setImageResource(
                R.drawable.wifi_icon
        );

    }

}
