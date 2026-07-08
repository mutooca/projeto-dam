package ao.uan.fc.dam.mobile.service;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import ao.uan.fc.dam.mobile.model.Packet;

/**
 * Implementa o protocolo Stop-and-Wait sobre UDP.
 * Garante que uma mensagem chegue ao destino através de confirmações e reenvios.
 */
public class ReliableUdpSender {
    private static final String TAG = "ReliableUdpSender";
    private static final int PORT = 8888;
    private static final int TIMEOUT = 2000; // 2 segundos
    private static final int MAX_ATTEMPTS = 3;

    public interface ReliableCallback {
        void onSuccess();
        void onFailure(String error);
    }

    /**
     * Envia um pacote e aguarda pelo ACK correspondente.
     */
    public void sendReliably(String ip, Packet packet, ReliableCallback callback) {
        new Thread(() -> {
            boolean delivered = false;
            int attempt = 0;

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT); // Configura o timeout de leitura
                InetAddress address = InetAddress.getByName(ip);
                byte[] sendData = packet.toJson().getBytes(StandardCharsets.UTF_8);

                while (attempt < MAX_ATTEMPTS && !delivered) {
                    attempt++;
                    Log.d(TAG, "Tentativa " + attempt + " para enviar: " + packet.getPacketId());

                    // 1. Enviar o pacote
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
                    socket.send(sendPacket);

                    // 2. Aguardar pelo ACK
                    byte[] receiveBuffer = new byte[2048];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                    try {
                        socket.receive(receivePacket);
                        String json = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                        Packet response = Packet.fromJson(json);

                        // 3. Verificar se é o ACK do nosso pacote
                        if ("ACK".equals(response.getType()) || "HELLO_ACK".equals(response.getType())) {
                            String targetId = (String) response.getPayload().get("targetPacketId");
                            if (packet.getPacketId().equals(targetId) || "HELLO_ACK".equals(response.getType())) {
                                Log.i(TAG, "ACK recebido com sucesso!");
                                delivered = true;
                                if (callback != null) callback.onSuccess();
                            }
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        Log.w(TAG, "Timeout na tentativa " + attempt);
                        if (attempt >= MAX_ATTEMPTS && callback != null) {
                            callback.onFailure("Máximo de tentativas excedido.");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro na transferência fiável", e);
                if (callback != null) callback.onFailure(e.getMessage());
            }
        }).start();
    }
}
