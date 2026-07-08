package ao.uan.fc.dam.mobile.model;

import java.util.UUID;

public class CoordenadaGps {
    UUID id_coordenada;
    double latitude;
    double longitude;
    double raio;

    public CoordenadaGps(){}

    public CoordenadaGps(UUID id_coordenada, double latitude, double longitude, double raio) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRaio() {
        return raio;
    }

    public void setRaio(double raio) {
        this.raio = raio;
    }
}
