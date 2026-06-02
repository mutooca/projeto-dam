package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity(tableName = "utilizadores")
public class Utilizador implements Serializable {
    @PrimaryKey
    @NonNull
    private Long id;
    private String nome;
    private String email;
    private float saldo;
    private String palavra_chave;
    private LocalDate data_criacao;
    private LocalDate data_ultimo_anuncio;
    private String estado;

    public Utilizador(){
        this.id = 0L;
    }

    public Utilizador(@NonNull Long id, String nome, String email, float saldo,
                      String palavra_chave, LocalDate data_criacao,
                      LocalDate data_ultimo_anuncio, String estado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.saldo = saldo;
        this.palavra_chave = palavra_chave;
        this.data_criacao = data_criacao;
        this.data_ultimo_anuncio = data_ultimo_anuncio;
        this.estado = estado;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getPalavra_chave() {
        return palavra_chave;
    }

    public void setPalavra_chave(String palavra_chave) {
        this.palavra_chave = palavra_chave;
    }

    public LocalDate getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(LocalDate data_criacao) {
        this.data_criacao = data_criacao;
    }

    public LocalDate getData_ultimo_anuncio() {
        return data_ultimo_anuncio;
    }

    public void setData_ultimo_anuncio(LocalDate data_ultimo_anuncio) {
        this.data_ultimo_anuncio = data_ultimo_anuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
