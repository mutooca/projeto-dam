package ao.uan.fc.dam.mobile.model;

import java.time.LocalDateTime;

public class Historico {
    private String tipo;
    private int pontos;
    private String nome;
    private LocalDateTime dia;

    public Historico(String tipo, int pontos, String nome, LocalDateTime dia) {
        this.tipo = tipo;
        this.pontos = pontos;
        this.nome = nome;
        this.dia = dia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDia() {
        return dia;
    }

    public void setDia(LocalDateTime dia) {
        this.dia = dia;
    }
}
