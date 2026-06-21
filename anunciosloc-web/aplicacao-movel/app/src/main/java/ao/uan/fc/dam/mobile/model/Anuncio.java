package ao.uan.fc.dam.mobile.model;

import java.time.LocalDate;
import java.util.UUID;

public class Anuncio {
    private UUID id_anuncio;
    private String titulo;
    private String conteudo;
    private LocalDate data_publicacao;
    private String estado_anuncio;
    private String nome_local;
    private int total_entrega;

    private int pontos;
    private String criador;

    public Anuncio(){}

    public Anuncio(UUID id_anuncio, String titulo, String conteudo, LocalDate data_publicacao,
                   String estado_anuncio, String nome_local, int total_entrega,int pontos, String criador) {
        this.id_anuncio = id_anuncio;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.data_publicacao = data_publicacao;
        this.estado_anuncio = estado_anuncio;
        this.nome_local = nome_local;
        this.total_entrega = total_entrega;
        this.pontos  = pontos;
        this.criador = criador;
    }

    public UUID getId_anuncio() {
        return id_anuncio;
    }

    public void setId_anuncio(UUID id_anuncio) {
        this.id_anuncio = id_anuncio;
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

    public LocalDate getData_publicacao() {
        return data_publicacao;
    }

    public void setData_publicacao(LocalDate data_publicacao) {
        this.data_publicacao = data_publicacao;
    }

    public String getEstado_anuncio() {
        return estado_anuncio;
    }

    public void setEstado_anuncio(String estado_anuncio) {
        this.estado_anuncio = estado_anuncio;
    }

    public String getNome_local() {
        return nome_local;
    }

    public void setNome_local(String nome_local) {
        this.nome_local = nome_local;
    }

    public int getTotal_entrega() {
        return total_entrega;
    }

    public void setTotal_entrega(int total_entrega) {
        this.total_entrega = total_entrega;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }
}
