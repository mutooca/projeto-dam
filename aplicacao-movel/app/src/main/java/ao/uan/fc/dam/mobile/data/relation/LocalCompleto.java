package ao.uan.fc.dam.mobile.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.entity.Local;

public class LocalCompleto {

    @Embedded
    public Local local;

    @Relation(
            parentColumn = "id_local",
            entityColumn = "id_local"
    )
    public CoordenadaGps coordenadaGps;

    @Relation(
            parentColumn = "id_local",
            entityColumn = "id_local"
    )
    public CoordenadaWifi coordenadaWifi;

    public LocalCompleto() {
    }

    public LocalCompleto(Local local, CoordenadaGps coordenadaGps, CoordenadaWifi coordenadaWifi) {
        this.local = local;
        this.coordenadaGps = coordenadaGps;
        this.coordenadaWifi = coordenadaWifi;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public CoordenadaGps getCoordenadaGps() {
        return coordenadaGps;
    }

    public void setCoordenadaGps(CoordenadaGps coordenadaGps) {
        this.coordenadaGps = coordenadaGps;
    }

    public CoordenadaWifi getCoordenadaWifi() {
        return coordenadaWifi;
    }

    public void setCoordenadaWifi(CoordenadaWifi coordenadaWifi) {
        this.coordenadaWifi = coordenadaWifi;
    }
}