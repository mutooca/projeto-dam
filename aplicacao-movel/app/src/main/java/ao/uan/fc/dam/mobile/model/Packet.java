package ao.uan.fc.dam.mobile.model;

import com.google.gson.Gson;
import java.util.Map;
import java.util.UUID;

/**
 * Representa mensagens trocadas via UDP no protocolo P2P.
 */
public class Packet {
    private String packetId;
    private String type;
    private String senderId;
    private long timestamp;
    private Map<String, Object> payload;

    public Packet() {}

    public Packet(String type, String senderId, Map<String, Object> payload) {
        this.packetId = UUID.randomUUID().toString();
        this.type = type;
        this.senderId = senderId;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Packet fromJson(String json) {
        return new Gson().fromJson(json, Packet.class);
    }

    // Getters
    public String getPacketId() { return packetId; }
    public String getType() { return type; }
    public String getSenderId() { return senderId; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Object> getPayload() { return payload; }
}
