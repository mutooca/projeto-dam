package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(tableName = "anuncios")
public class Anuncio implements Serializable {
    @PrimaryKey
    @NonNull
    @SerializedName(value = "idAnuncio", alternate = {"id"})
    private UUID idAnuncio;
    
    @SerializedName("titulo")
    private String titulo;
    
    @SerializedName("conteudo")
    private String conteudo;
    
    @SerializedName("dataPublicacao")
    private LocalDateTime dataPublicacao;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("categoria")
    private String categoria; 
    
    @SerializedName(value = "modoEntrega", alternate = {"modo_entrega"})
    private String modo_entrega;
    
    @SerializedName("pontos")
    private int pontos;
    
    @SerializedName(value = "restricaoPerfil", alternate = {"politicaFiltro"})
    private String restricaoPerfil; 

    @SerializedName("tipoPolitica")
    private String tipoPolitica;

    @SerializedName(value = "nomeLocal", alternate = {"localNome"})
    private String nomeLocal;
    
    @Ignore
    private Local local;
    @Ignore
    private Utilizador autor;

    private String usuarioEmail; 
    
    @SerializedName("autorEmail")
    private String autorEmail;

    public Anuncio() {
        this.idAnuncio = UUID.randomUUID();
    }

    @NonNull
    public UUID getIdAnuncio() { return idAnuncio; }
    public void setIdAnuncio(@NonNull UUID idAnuncio) { this.idAnuncio = idAnuncio; }

    public void setId(UUID id) { this.idAnuncio = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getModo_entrega() { return modo_entrega; }
    public void setModo_entrega(String modo_entrega) { this.modo_entrega = modo_entrega; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public String getRestricaoPerfil() { return restricaoPerfil; }
    public void setRestricaoPerfil(String restricaoPerfil) { this.restricaoPerfil = restricaoPerfil; }

    public String getTipoPolitica() { return tipoPolitica; }
    public void setTipoPolitica(String tipoPolitica) { this.tipoPolitica = tipoPolitica; }

    public String getNomeLocal() { return nomeLocal; }
    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }

    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    public Utilizador getAutor() { return autor; }
    public void setAutor(Utilizador autor) { 
        this.autor = autor;
        if (autor != null) this.autorEmail = autor.getEmail();
    }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public String getAutorEmail() { return autorEmail; }
    public void setAutorEmail(String autorEmail) { this.autorEmail = autorEmail; }

    public String getNome_local() {
        if (local != null) return local.getNome();
        return nomeLocal != null ? nomeLocal : "N/D";
    }
}
