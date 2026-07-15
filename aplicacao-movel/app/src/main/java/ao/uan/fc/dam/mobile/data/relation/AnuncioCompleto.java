package ao.uan.fc.dam.mobile.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;

public class AnuncioCompleto {

    @Embedded
    public Anuncio anuncio;

    @Relation(
            parentColumn = "id_local",
            entityColumn = "id_local"
    )
    public Local local;

    @Relation(
            parentColumn = "id_utilizador",
            entityColumn = "id_utilizador"
    )
    public Utilizador autor;

    public AnuncioCompleto() {
    }

    public AnuncioCompleto(Anuncio anuncio, Local local, Utilizador autor) {
        this.anuncio = anuncio;
        this.local = local;
        this.autor = autor;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncio anuncio) {
        this.anuncio = anuncio;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Utilizador getAutor() {
        return autor;
    }

    public void setAutor(Utilizador autor) {
        this.autor = autor;
    }
}