package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(tableName = "anuncios")
public class Anuncio implements Serializable {
    @PrimaryKey
    @NonNull
    private UUID idAnuncio;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private String estado;
    private String categoria; // Usado para "WHITELIST" ou "BLACKLIST" no modo P2P
    private String modo_entrega;
    private int pontos;
    private String restricaoPerfil; // Requisito 2.1.3: Lista de chaves de perfil
    
    @Ignore
    private Local local;
    @Ignore
    private Utilizador autor;

    private String usuarioEmail; // Email do utilizador que está a ver (cache context)
    private String autorEmail;   // Email de quem criou o anúncio

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
        return local != null ? local.getNome() : "N/D";
    }
}
