package ao.uan.fc.dam.mobile.model;

import java.util.UUID;

public class CoordendaWifi {
    UUID id_coordenada_wifi;
    String ssid;

    public CoordendaWifi(){}

    public CoordendaWifi(UUID id_coordenada_wifi, String ssid) {
        this.id_coordenada_wifi = id_coordenada_wifi;
        this.ssid = ssid;
    }

    public UUID getId_coordenada_wifi() {
        return id_coordenada_wifi;
    }

    public void setId_coordenada_wifi(UUID id_coordenada_wifi) {
        this.id_coordenada_wifi = id_coordenada_wifi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
