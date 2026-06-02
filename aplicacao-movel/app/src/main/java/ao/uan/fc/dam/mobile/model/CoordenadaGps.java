package ao.uan.fc.dam.mobile.model;

import java.util.UUID;

public class CoordenadaGps {
    private UUID id_coordenada;
    private double latitude;
    private double longitude;
    private int raio;

    public CoordenadaGps() {
    }

    public CoordenadaGps(UUID id_coordenada, double latitude, double longitude, int raio) {
        this.id_coordenada = id_coordenada;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
    }

    public UUID getId_coordenada() {
        return this.id_coordenada;
    }

    public void setId_coordenada(UUID id_coordenada) {
        this.id_coordenada = id_coordenada;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRaio() {
        return this.raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }
}