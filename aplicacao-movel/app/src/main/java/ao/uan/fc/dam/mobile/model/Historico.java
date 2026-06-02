package ao.uan.fc.dam.mobile.model;

import java.time.LocalDateTime;

/* JADX INFO: loaded from: classes8.dex */
public class Historico {
    private LocalDateTime dia;
    private String nome;
    private int pontos;
    private String tipo;

    public Historico() {
    }

    public Historico(String tipo, int pontos, String nome, LocalDateTime dia) {
        this.tipo = tipo;
        this.pontos = pontos;
        this.nome = nome;
        this.dia = dia;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPontos() {
        return this.pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDia() {
        return this.dia;
    }

    public void setDia(LocalDateTime dia) {
        this.dia = dia;
    }
}