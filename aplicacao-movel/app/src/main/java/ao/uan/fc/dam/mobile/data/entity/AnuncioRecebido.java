package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "anuncios_recebidos")
public class AnuncioRecebido {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_anuncio_recebido")
    private int idAnuncioRecebido;

    @ColumnInfo(name = "msg_id")
    private String msgId;

    @ColumnInfo(name = "id_utilizador")
    private int idUtilizador;

    @ColumnInfo(name = "modo_entrega")
    private String modoEntrega;

    @ColumnInfo(name = "autor")
    private String autor;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "conteudo")
    private String conteudo;

    @ColumnInfo(name = "local")
    private String local;

    @ColumnInfo(name = "politica_tipo")
    private String politicaTipo;

    @ColumnInfo(name = "politica_chaves")
    private String politicaChaves;

    @ColumnInfo(name = "data_rececao")
    private LocalDateTime dataRececao;

    @ColumnInfo(name = "data_inicio")
    private LocalDateTime dataInicio;

    @ColumnInfo(name = "data_fim")
    private LocalDateTime dataFim;

    public AnuncioRecebido() {
    }

    public int getIdAnuncioRecebido() {
        return idAnuncioRecebido;
    }

    public void setIdAnuncioRecebido(int idAnuncioRecebido) {
        this.idAnuncioRecebido = idAnuncioRecebido;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getModoEntrega() {
        return modoEntrega;
    }

    public void setModoEntrega(String modoEntrega) {
        this.modoEntrega = modoEntrega;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getPoliticaTipo() {
        return politicaTipo;
    }

    public void setPoliticaTipo(String politicaTipo) {
        this.politicaTipo = politicaTipo;
    }

    public String getPoliticaChaves() {
        return politicaChaves;
    }

    public void setPoliticaChaves(String politicaChaves) {
        this.politicaChaves = politicaChaves;
    }

    public LocalDateTime getDataRececao() {
        return dataRececao;
    }

    public void setDataRececao(LocalDateTime dataRececao) {
        this.dataRececao = dataRececao;
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
}
