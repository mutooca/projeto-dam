package ao.uan.fc.dam.mobile.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(
        tableName = "utilizadores",
        indices = {
                @Index(value = "email", unique = true)
        }
)
public class Utilizador {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_utilizador")
    private int idUtilizador;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "palavra_chave")
    private String palavraChave;

    @ColumnInfo(name = "saldo")
    private Integer saldo;

    @ColumnInfo(name = "data_criacao")
    private LocalDateTime dataCriacao;

    public Utilizador() {
    }

    public Utilizador(int idUtilizador, String nome, String email, String palavraChave, Integer saldo, LocalDateTime dataCriacao) {
        this.idUtilizador = idUtilizador;
        this.nome = nome;
        this.email = email;
        this.palavraChave = palavraChave;
        this.saldo = saldo;
        this.dataCriacao = dataCriacao;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int idUtilizador) {
        this.idUtilizador = idUtilizador;
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

    public String getPalavraChave() {
        return palavraChave;
    }

    public void setPalavraChave(String palavraChave) {
        this.palavraChave = palavraChave;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}