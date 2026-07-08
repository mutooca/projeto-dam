package ao.uan.fc.dam.mobile.service;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import ao.uan.fc.dam.mobile.model.Packet;

public class UdpServer extends Thread {
    private static final String TAG = "UdpServer";
    private final int PORT = 8888;
    private boolean running = true;
    
    private final DiscoveryManager discoveryManager;
    private final AdvertisementManager advertisementManager;

    public UdpServer(DiscoveryManager discoveryManager, AdvertisementManager advertisementManager) {
        this.discoveryManager = discoveryManager;
        this.advertisementManager = advertisementManager;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[8192]; // Aumentado para anúncios maiores
            while (running) {
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagram);
                
                String remoteIp = datagram.getAddress().getHostAddress();
                String json = new String(datagram.getData(), 0, datagram.getLength(), StandardCharsets.UTF_8);
                Packet packet = Packet.fromJson(json);
                
                if (packet == null) continue;

                Log.d(TAG, "Pacote recebido: " + packet.getType() + " de " + remoteIp);

                switch (packet.getType()) {
                    case "HELLO":
                    case "HELLO_ACK":
                    case "PROFILE_REQUEST":
                    case "PROFILE_RESPONSE":
                        discoveryManager.handleIncomingPacket(packet, remoteIp);
                        break;
                    case "ADVERTISEMENT":
                        advertisementManager.handleIncomingAdvertisement(packet, remoteIp);
                        break;
                    case "ACK":
                        Log.i(TAG, "Entrega confirmada pelo vizinho para o pacote: " + packet.getPayload().get("targetPacketId"));
                        break;
                }
            }
        } catch (Exception e) {
            if (running) Log.e(TAG, "Erro no socket UDP", e);
        }
    }

    public void stopServer() { running = false; this.interrupt(); }
}
