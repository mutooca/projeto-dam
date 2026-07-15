package ao.uan.fc.dam.mobile.network.dto;

public class EditarLocalResponse {

    private String idLocal;
    private String nome;
    private Double latitude;
    private Double longitude;
    private Double raio;
    private String ssidWifi;
    private boolean sucesso;
    private String mensagem;

    public String getIdLocal() {
        return idLocal;
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

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
