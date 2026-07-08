package ao.uan.fc.dam.mobile.model;

/**
 * Representa um dispositivo vizinho descoberto na rede P2P.
 */
public class Neighbor {
    private String id;
    private String ipAddress;
    private long lastSeen;

    public Neighbor(String id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.lastSeen = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getIpAddress() { return ipAddress; }
    public long getLastSeen() { return lastSeen; }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Neighbor{" + "id='" + id + '\'' + ", ip='" + ipAddress + '\'' + '}';
    }
}
