package ao.uan.fc.dam.mobile.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.Historico;

public class HistoricoViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imgMode;
    private final TextView txtTitle;
    private final TextView txtRelativeTime;
    private final TextView txtPoints;

    public HistoricoViewHolder(@NonNull View itemView) {

        super(itemView);

        imgMode = itemView.findViewById(R.id.imgMode);
        txtTitle = itemView.findViewById(R.id.txtTitleLocal);
        txtRelativeTime = itemView.findViewById(R.id.txtRelativeTime);
        txtPoints = itemView.findViewById(R.id.txtPoints);

    }

    public void bind(Historico historico){

        txtTitle.setText(historico.getNome());

        txtRelativeTime.setText(
                historico.getRegisto()
                        .toLocalDate()
                        .toString()
        );

        if(historico.getPontos() >= 0){

            txtPoints.setText("+" + historico.getPontos());

            imgMode.setImageResource(R.drawable.trending_up_icon);

        }else{

            txtPoints.setText(String.valueOf(historico.getPontos()));

            imgMode.setImageResource(R.drawable.trending_down_icon);

        }

    }

}