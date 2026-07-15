package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
        tableName = "coordenada_wifi",
        foreignKeys = @ForeignKey(
                entity = Local.class,
                parentColumns = "id_local",
                childColumns = "id_local",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index("id_local")
        }
)
public class CoordenadaWifi {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_coordenada_wifi")
    private int idCoordenadaWifi;

    @ColumnInfo(name = "ssid")
    private String ssid;

    @ColumnInfo(name = "id_local")
    private int idLocal;

    public CoordenadaWifi() {
    }

    public CoordenadaWifi(int idCoordenadaWifi, String ssid, int idLocal) {
        this.idCoordenadaWifi = idCoordenadaWifi;
        this.ssid = ssid;
        this.idLocal = idLocal;
    }

    public int getIdCoordenadaWifi() {
        return idCoordenadaWifi;
    }

    public void setIdCoordenadaWifi(int idCoordenadaWifi) {
        this.idCoordenadaWifi = idCoordenadaWifi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }
}