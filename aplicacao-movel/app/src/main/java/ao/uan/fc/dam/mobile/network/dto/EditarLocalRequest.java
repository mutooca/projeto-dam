package ao.uan.fc.dam.mobile.network.dto;

public class EditarLocalRequest {

    private final String nome;
    private final Double latitude;
    private final Double longitude;
    private final Double raio;
    private final String ssidWifi;

    public EditarLocalRequest(
            String nome,
            Double latitude,
            Double longitude,
            Double raio,
            String ssidWifi
    ) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
        this.ssidWifi = ssidWifi;
    }

    public String getNome() {
        return nome;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRaio() {
        return raio;
    }

    public String getSsidWifi() {
        return ssidWifi;
    }
}
