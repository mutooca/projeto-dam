package ao.uan.fc.dam.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;
import ao.uan.fc.dam.mobile.ui.viewholder.AnuncioViewHolder;


public class AnuncioAdapter
        extends RecyclerView.Adapter<AnuncioViewHolder>{


    private List<AnuncioModel> anuncios = new ArrayList<>();

    private OnAnuncioClickListener listener;



    public interface OnAnuncioClickListener{

        void onClick(AnuncioModel anuncio);

        void onLongClick(AnuncioModel anuncio);

    }



    public void setOnAnuncioClickListener(
            OnAnuncioClickListener listener){

        this.listener = listener;

    }



    public void setAnuncios(
            List<AnuncioModel> lista){

        anuncios = lista;

        notifyDataSetChanged();

    }



    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){


        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_inicio_fragment,
                                parent,
                                false
                        );


        return new AnuncioViewHolder(view);

    }



    @Override
    public void onBindViewHolder(
            @NonNull AnuncioViewHolder holder,
            int position){


        AnuncioModel anuncio =
                anuncios.get(position);



        holder.bind(anuncio);



        holder.itemView.setOnClickListener(v -> {

            if(listener != null){

                listener.onClick(anuncio);

            }

        });



        holder.itemView.setOnLongClickListener(v -> {


            if(listener != null){

                listener.onLongClick(anuncio);

            }


            return true;

        });

    }



    @Override
    public int getItemCount(){

        return anuncios.size();

    }

}