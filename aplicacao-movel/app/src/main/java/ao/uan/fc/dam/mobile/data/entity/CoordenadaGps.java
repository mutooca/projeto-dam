package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;


@Entity(
        tableName = "coordenada_gps",
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
public class CoordenadaGps {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_coordenada_gps")
    private int idCoordenadaGps;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "raio")
    private double raio;

    @ColumnInfo(name = "id_local")
    private int idLocal;

    public CoordenadaGps() {
    }

    public CoordenadaGps(int idCoordenadaGps, double latitude, double longitude, double raio, int idLocal) {
        this.idCoordenadaGps = idCoordenadaGps;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
        this.idLocal = idLocal;
    }

    public int getIdCoordenadaGps() {
        return idCoordenadaGps;
    }

    public void setIdCoordenadaGps(int idCoordenadaGps) {
        this.idCoordenadaGps = idCoordenadaGps;
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

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }
}