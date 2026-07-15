package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "atributos_perfil",
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
public class AtributoPerfil {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_atributo")
    private int idAtributo;

    @ColumnInfo(name = "chave")
    private String chave;

    @ColumnInfo(name = "valor")
    private String valor;

    @ColumnInfo(name = "id_utilizador")
    private int idUtilizador;

    public AtributoPerfil() {
    }

    public AtributoPerfil(String chave, String valor, int idUtilizador) {
        this.chave = chave;
        this.valor = valor;
        this.idUtilizador = idUtilizador;
    }

    public int getIdAtributo() {
        return idAtributo;
    }

    public void setIdAtributo(int idAtributo) {
        this.idAtributo = idAtributo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(int idUtilizador) {
        this.idUtilizador = idUtilizador;
    }
}