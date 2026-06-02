package ao.uan.fc.dam.mobile.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "locais")
public class Local implements Serializable {
    private Integer capacidade;
    private Integer conexoesDisponiveis;
    @Ignore
    private CoordenadaGps coordenada_gps;
    private String descricao;
    private Double distanciaKm;
    @PrimaryKey
    private Long id;
    private UUID id_local;
    private String infraestrutura;
    private Double latitude;
    private Double longitude;
    private String nome;
    private Integer totalAnuncios;
    private Integer totalEntregas;

    public Local() {
    }

    @Ignore
    public Local(UUID id_local, String nome, String descricao, String infraestrutura, CoordenadaGps coordenada_gps) {
        this.id_local = id_local;
        this.nome = nome;
        this.descricao = descricao;
        this.infraestrutura = infraestrutura;
        this.coordenada_gps = coordenada_gps;
    }

    public UUID getId_local() {
        return this.id_local;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId_local(UUID id_local) {
        this.id_local = id_local;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInfraestrutura() {
        return this.infraestrutura;
    }

    public void setInfraestrutura(String infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public CoordenadaGps getCoordenada_gps() {
        return this.coordenada_gps;
    }

    public void setCoordenada_gps(CoordenadaGps coordenada_gps) {
        this.coordenada_gps = coordenada_gps;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCapacidade() {
        return this.capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Integer getConexoesDisponiveis() {
        return this.conexoesDisponiveis;
    }

    public void setConexoesDisponiveis(Integer conexoesDisponiveis) {
        this.conexoesDisponiveis = conexoesDisponiveis;
    }

    public Double getDistanciaKm() {
        return this.distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getTotalAnuncios() {
        return this.totalAnuncios;
    }

    public void setTotalAnuncios(Integer totalAnuncios) {
        this.totalAnuncios = totalAnuncios;
    }

    public Integer getTotalEntregas() {
        return this.totalEntregas;
    }

    public void setTotalEntregas(Integer totalEntregas) {
        this.totalEntregas = totalEntregas;
    }
}
