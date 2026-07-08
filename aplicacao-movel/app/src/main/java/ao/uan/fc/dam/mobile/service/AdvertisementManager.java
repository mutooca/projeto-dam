package ao.uan.fc.dam.mobile.service;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Packet;

/**
 * Gere a transferência de anúncios entre dispositivos P2P.
 */
public class AdvertisementManager {
    private static final String TAG = "AdvertisementManager";
    private final Context context;
    private final UdpClient udpClient;
    private final String myEmail;
    private final Gson gson = new Gson();

    public AdvertisementManager(Context context, String myEmail) {
        this.context = context;
        this.udpClient = new UdpClient();
        this.myEmail = myEmail;
    }

    /**
     * Envia um anúncio para um vizinho específico.
     */
    public void sendAdvertisement(String targetIp, Anuncio anuncio) {
        Log.d(TAG, "A enviar anúncio: " + anuncio.getTitulo() + " para " + targetIp);
        
        // Converter Anuncio para Map para o Payload
        String jsonAd = gson.toJson(anuncio);
        Map<String, Object> payload = gson.fromJson(jsonAd, new TypeToken<Map<String, Object>>(){}.getType());
        
        Packet packet = new Packet("ADVERTISEMENT", myEmail, payload);
        udpClient.sendPacket(targetIp, packet);
    }

    /**
     * Processa a receção de um anúncio e envia confirmação (ACK).
     */
    public void handleIncomingAdvertisement(Packet packet, String remoteIp) {
        try {
            Log.d(TAG, "Recebido anúncio de " + packet.getSenderId());
            
            // 1. Converter Payload de volta para Anuncio
            String jsonAd = gson.toJson(packet.getPayload());
            Anuncio anuncio = gson.fromJson(jsonAd, Anuncio.class);
            
            // 2. Guardar no Room
            guardarAnuncioNoBanco(anuncio);
            
            // 3. Enviar ACK
            Map<String, Object> ackPayload = new HashMap<>();
            ackPayload.put("targetPacketId", packet.getPacketId());
            Packet ack = new Packet("ACK", myEmail, ackPayload);
            udpClient.sendPacket(remoteIp, ack);
            
            Log.i(TAG, "Anúncio guardado e ACK enviado para " + remoteIp);
            
        } catch (Exception e) {
            Log.e(TAG, "Falha ao processar anúncio recebido", e);
        }
    }

    private void guardarAnuncioNoBanco(Anuncio anuncio) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                anuncio.setUsuarioEmail(myEmail); // Definir o contexto local
                db.anuncioDao().insertAll(List.of(anuncio));
            } catch (Exception e) {
                Log.e(TAG, "Erro ao inserir no Room", e);
            }
        }).start();
    }
}
