package ao.uan.fc.dam.mobile.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "anuncios")
public class Anuncio implements Serializable {
    private String autor;
    private String categoria;
    private LocalDateTime data_publicacao;
    private boolean entregue;
    private String estado_anuncio;
    @PrimaryKey
    private Long id;
    private UUID id_anuncio;
    private String mensagem;
    private String nome_local;
    private int pontos;
    private String titulo;
    private int total_entrega;
    private String usuarioEmail;

    public Anuncio() {
    }

    public Anuncio(UUID id_anuncio, String titulo, String conteudo, LocalDateTime data_publicacao, String estado_anuncio, String nome_local, int total_entrega, int pontos, String criador) {
        this.id_anuncio = id_anuncio;
        this.titulo = titulo;
        this.mensagem = conteudo;
        this.data_publicacao = data_publicacao;
        this.estado_anuncio = estado_anuncio;
        this.nome_local = nome_local;
        this.total_entrega = total_entrega;
        this.pontos = pontos;
        this.autor = criador;
    }

    public UUID getId_anuncio() {
        return this.id_anuncio;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId_anuncio(UUID id_anuncio) {
        this.id_anuncio = id_anuncio;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setConteudo(String conteudo) {
        this.mensagem = conteudo;
    }

    public boolean isEntregue() {
        return this.entregue;
    }

    public void setEntregue(boolean entregue) {
        this.entregue = entregue;
        this.estado_anuncio = entregue ? "ENTREGUE" : "ATIVO";
    }

    public LocalDateTime getData_publicacao() {
        return this.data_publicacao;
    }

    public void setData_publicacao(LocalDateTime data_publicacao) {
        this.data_publicacao = data_publicacao;
    }

    public void setDataPost(LocalDateTime dataPost) {
        this.data_publicacao = dataPost;
    }

    public String getEstado_anuncio() {
        return this.estado_anuncio;
    }

    public void setEstado_anuncio(String estado_anuncio) {
        this.estado_anuncio = estado_anuncio;
    }

    public String getNome_local() {
        return this.nome_local;
    }

    public void setNome_local(String nome_local) {
        this.nome_local = nome_local;
    }

    public void setLocalNome(String localNome) {
        this.nome_local = localNome;
    }

    public int getTotal_entrega() {
        return this.total_entrega;
    }

    public void setTotal_entrega(int total_entrega) {
        this.total_entrega = total_entrega;
    }

    public int getPontos() {
        return this.pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getAutor() {
        return this.autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setAutorEmail(String autorEmail) {
        this.autor = autorEmail;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUsuarioEmail() {
        return this.usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }
}
