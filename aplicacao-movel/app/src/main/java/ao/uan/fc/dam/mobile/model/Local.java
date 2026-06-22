package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "locais")
public class Local implements Serializable {
    @PrimaryKey
    @NonNull
    private UUID idLocal;
    private String nome;
    
    // GPS (F3)
    private Double latitude;
    private Double longitude;
    private Integer raio;

    // WiFi (F3)
    private String ssidWifi;

    @Ignore
    private Utilizador criadoPor;

    public Local() {
        this.idLocal = UUID.randomUUID();
    }

    @NonNull public UUID getIdLocal() { return idLocal; }
    public void setIdLocal(@NonNull UUID idLocal) { this.idLocal = idLocal; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getRaio() { return raio; }
    public void setRaio(Integer raio) { this.raio = raio; }

    public String getSsidWifi() { return ssidWifi; }
    public void setSsidWifi(String ssidWifi) { this.ssidWifi = ssidWifi; }

    public Utilizador getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Utilizador criadoPor) { this.criadoPor = criadoPor; }

    public String getId() { return idLocal.toString(); }
    
    public boolean isWifi() { return ssidWifi != null && !ssidWifi.isEmpty(); }
}
