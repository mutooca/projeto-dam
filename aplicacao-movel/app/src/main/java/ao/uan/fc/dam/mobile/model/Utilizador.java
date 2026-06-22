package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(tableName = "utilizadores")
public class Utilizador implements Serializable {
    @PrimaryKey
    @NonNull
    private UUID idUtilizador;
    private String nome;
    private String email;
    private Integer saldo;
    private String role;
    private LocalDateTime dataCriacao;
    private String preferenciaAnuncio;
    
    // Contadores para o Perfil
    private Long totalAnuncios;
    private Long totalEntregas;

    public Utilizador() {
        this.idUtilizador = UUID.randomUUID();
    }

    @NonNull
    public UUID getIdUtilizador() { return idUtilizador; }
    public void setIdUtilizador(@NonNull UUID idUtilizador) { this.idUtilizador = idUtilizador; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getSaldo() { return saldo; }
    public void setSaldo(Integer saldo) { this.saldo = saldo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getPreferenciaAnuncio() { return preferenciaAnuncio; }
    public void setPreferenciaAnuncio(String preferenciaAnuncio) { this.preferenciaAnuncio = preferenciaAnuncio; }

    public Long getTotalAnuncios() { return totalAnuncios; }
    public void setTotalAnuncios(Long totalAnuncios) { this.totalAnuncios = totalAnuncios; }

    public Long getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(Long totalEntregas) { this.totalEntregas = totalEntregas; }
}
