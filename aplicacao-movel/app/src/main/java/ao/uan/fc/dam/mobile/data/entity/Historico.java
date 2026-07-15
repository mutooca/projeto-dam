package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Entity(
        tableName = "historico",
        foreignKeys = @ForeignKey(
                entity = Utilizador.class,
                parentColumns = "id_utilizador",
                childColumns = "id_utilizador",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index("id_utilizador")
        }
)
public class Historico {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_historico")
    private int idHistorico;

    @ColumnInfo(name = "tipo")
    private String tipo;

    @ColumnInfo(name = "pontos")
    private int pontos;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "registo")
    private LocalDateTime registo;

    @ColumnInfo(name = "id_utilizador")
    private int idUtilizador;

    public Historico() {
    }

    public Historico(int idHistorico, String tipo, int pontos, String nome, LocalDateTime registo, int idUtilizador) {
        this.idHistorico = idHistorico;
        this.tipo = tipo;
        this.pontos = pontos;
        this.nome = nome;
        this.registo = registo;
        this.idUtilizador = idUtilizador;
    }

    public int getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
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

    public LocalDateTime getRegisto() {
        return registo;
    }

    public void setRegisto(LocalDateTime registo) {
        this.registo = registo;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int idUtilizador) {
        this.idUtilizador = idUtilizador;
    }
}