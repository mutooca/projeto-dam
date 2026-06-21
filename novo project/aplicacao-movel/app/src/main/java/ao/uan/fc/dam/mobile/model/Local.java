package ao.uan.fc.dam.mobile.model;

import java.util.UUID;

public class Local {
    UUID id_local;
    String nome;
    String descricao;
    String infraestrutura;

    CoordendaWifi coordendad_wifi;
    CoordenadaGps coordenada_gps;

    public Local(){}
    public Local(UUID id_local, String nome, String descricao, String infraestrutura, CoordendaWifi coordendad_wifi, CoordenadaGps coordenada_gps) {
        this.id_local = id_local;
        this.nome = nome;
        this.descricao = descricao;
        this.infraestrutura = infraestrutura;
        this.coordendad_wifi = coordendad_wifi;
        this.coordenada_gps = coordenada_gps;
    }

    public UUID getId_local() {
        return id_local;
    }

    public void setId_local(UUID id_local) {
        this.id_local = id_local;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInfraestrutura() {
        return infraestrutura;
    }

    public void setInfraestrutura(String infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public CoordendaWifi getCoordendad_wifi() {
        return coordendad_wifi;
    }

    public void setCoordendad_wifi(CoordendaWifi coordendad_wifi) {
        this.coordendad_wifi = coordendad_wifi;
    }

    public CoordenadaGps getCoordenada_gps() {
        return coordenada_gps;
    }

    public void setCoordenada_gps(CoordenadaGps coordenada_gps) {
        this.coordenada_gps = coordenada_gps;
    }


}
