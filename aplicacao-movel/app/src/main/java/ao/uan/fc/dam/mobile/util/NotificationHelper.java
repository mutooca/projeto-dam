package ao.uan.fc.dam.mobile.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.activity.DashboardActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "anuncios_descentralizados";
    private static final String CHANNEL_NAME = "Anúncios Próximos";
    private static final String CHANNEL_DESC = "Notificações de anúncios recebidos via P2P";

    public static void criarCanalNotificacao(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void mostrarNotificacaoAnuncio(Context context, String msgId, String titulo, String conteudo) {
        criarCanalNotificacao(context);

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra("msg_id", msgId);
        intent.putExtra("abrir_detalhe", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                msgId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle("Novo anúncio perto de si")
                .setContentText(titulo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(msgId.hashCode(), builder.build());
        } catch (SecurityException e) {
            // Falha silenciosa se permissão não foi concedida (Android 13+)
        }
    }
}
