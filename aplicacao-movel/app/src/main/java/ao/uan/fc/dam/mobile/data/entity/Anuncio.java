package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import ao.uan.fc.dam.mobile.data.enums.EstadoAnuncio;
import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;
import ao.uan.fc.dam.mobile.data.enums.Visibilidade;

@Entity(
        tableName = "anuncios",
        foreignKeys = {
                @ForeignKey(
                        entity = Utilizador.class,
                        parentColumns = "id_utilizador",
                        childColumns = "id_utilizador",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Local.class,
                        parentColumns = "id_local",
                        childColumns = "id_local",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("id_utilizador"),
                @Index("id_local")
        }
)
public class Anuncio {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_anuncio")
    private int idAnuncio;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "conteudo")
    private String conteudo;

    @ColumnInfo(name = "estado")
    private EstadoAnuncio estado;

    @ColumnInfo(name = "modo_entrega")
    private ModoEntrega modoEntrega;

    @ColumnInfo(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @ColumnInfo(name = "data_inicio")
    private LocalDateTime dataInicio;

    @ColumnInfo(name = "data_fim")
    private LocalDateTime dataFim;

    @ColumnInfo(name = "restricao_perfil")
    private String restricaoPerfil;

    @ColumnInfo(name = "visibilidade")
    private Visibilidade visibilidade;

    @ColumnInfo(name = "id_utilizador")
    private int idUtilizador;

    @ColumnInfo(name = "id_local")
    private int idLocal;

    @ColumnInfo(name = "id_servidor")
    private String idServidor;

    public Anuncio() {
    }

    public int getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(int idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public EstadoAnuncio getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnuncio estado) {
        this.estado = estado;
    }

    public ModoEntrega getModoEntrega() {
        return modoEntrega;
    }

    public void setModoEntrega(ModoEntrega modoEntrega) {
        this.modoEntrega = modoEntrega;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public String getRestricaoPerfil() {
        return restricaoPerfil;
    }

    public void setRestricaoPerfil(String restricaoPerfil) {
        this.restricaoPerfil = restricaoPerfil;
    }

    public Visibilidade getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(Visibilidade visibilidade) {
        this.visibilidade = visibilidade;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(String idServidor) {
        this.idServidor = idServidor;
    }
}