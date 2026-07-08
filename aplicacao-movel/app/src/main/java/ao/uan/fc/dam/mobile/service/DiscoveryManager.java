package ao.uan.fc.dam.mobile.service;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ao.uan.fc.dam.mobile.model.Neighbor;
import ao.uan.fc.dam.mobile.model.Packet;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.database.AppDatabase;

public class DiscoveryManager {
    private static final String TAG = "DiscoveryManager";
    private final String myId;
    private final Context context;
    private final UdpClient udpClient = new UdpClient();
    private final Map<String, Neighbor> neighborMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> neighborProfiles = new ConcurrentHashMap<>();

    public DiscoveryManager(Context context, String myId) {
        this.context = context;
        this.myId = myId;
    }

    public void startDiscovery(String targetIp) {
        udpClient.sendPacket(targetIp, new Packet("HELLO", myId, new HashMap<>()));
    }

    public void handleIncomingPacket(Packet packet, String remoteIp) {
        switch (packet.getType()) {
            case "HELLO":
                addNeighbor(packet.getSenderId(), remoteIp);
                udpClient.sendPacket(remoteIp, new Packet("HELLO_ACK", myId, new HashMap<>()));
                break;
            case "HELLO_ACK":
                addNeighbor(packet.getSenderId(), remoteIp);
                // Solicitar perfil imediatamente
                udpClient.sendPacket(remoteIp, new Packet("PROFILE_REQUEST", myId, new HashMap<>()));
                break;
            case "PROFILE_REQUEST":
                enviarMeuPerfil(remoteIp);
                break;
            case "PROFILE_RESPONSE":
                Map<String, String> profile = (Map<String, String>) packet.getPayload().get("attributes");
                neighborProfiles.put(packet.getSenderId(), profile);
                Log.i(TAG, "Perfil recebido do vizinho " + packet.getSenderId() + ": " + profile);
                break;
        }
    }

    private void addNeighbor(String id, String ip) {
        if (!neighborMap.containsKey(id)) {
            neighborMap.put(id, new Neighbor(id, ip));
        }
    }

    private void enviarMeuPerfil(String remoteIp) {
        new Thread(() -> {
            Utilizador eu = AppDatabase.getInstance(context).utilizadorDao().getProfile();
            Map<String, Object> payload = new HashMap<>();
            payload.put("attributes", eu != null ? eu.getAtributos() : new HashMap<>());
            udpClient.sendPacket(remoteIp, new Packet("PROFILE_RESPONSE", myId, payload));
        }).start();
    }

    public Map<String, Map<String, String>> getNeighborProfiles() { return neighborProfiles; }
    public Map<String, Neighbor> getNeighborMap() { return neighborMap; }
}
