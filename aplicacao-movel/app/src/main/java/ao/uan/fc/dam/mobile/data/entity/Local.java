package ao.uan.fc.dam.mobile.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ao.uan.fc.dam.mobile.data.enums.TipoCoordenada;

@Entity(tableName = "locais")
public class Local {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_local")
    private int idLocal;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "id_servidor")
    private String idServidor;

    @ColumnInfo(name = "tipo_coordenada")
    private TipoCoordenada tipoCoordenada;

    public Local() {
    }

    public Local(String nome, TipoCoordenada tipoCoordenada) {
        this.nome = nome;
        this.tipoCoordenada = tipoCoordenada;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(String idServidor) {
        this.idServidor = idServidor;
    }

    public TipoCoordenada getTipoCoordenada() {
        return tipoCoordenada;
    }

    public void setTipoCoordenada(TipoCoordenada tipoCoordenada) {
        this.tipoCoordenada = tipoCoordenada;
    }
}
