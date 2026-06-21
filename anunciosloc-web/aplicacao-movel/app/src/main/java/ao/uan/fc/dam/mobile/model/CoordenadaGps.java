package ao.uan.fc.dam.mobile.model;

import java.util.UUID;

public class CoordenadaGps {
    UUID id_coordenada;
    String latitude;
    String longitude;
    int raio;

    public CoordenadaGps(){}

    public CoordenadaGps(UUID id_coordenada, String latitude, String longitude, int raio) {
        this.id_coordenada = id_coordenada;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
    }

    public UUID getId_coordenada() {
        return id_coordenada;
    }

    public void setId_coordenada(UUID id_coordenada) {
        this.id_coordenada = id_coordenada;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getRaio() {
        return raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }
}
