package ao.uan.fc.dam.mobile.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    
    // Perfil dinâmico (Chave-Valor) para filtragem P2P
    private Map<String, String> atributos = new HashMap<>();
    
    // Contadores para o Perfil
    private Long totalAnuncios;
    private Long totalEntregas;

    public Utilizador() {
        this.idUtilizador = UUID.randomUUID();
    }

    /**
     * Adiciona um novo atributo ao perfil.
     */
    public void adicionarAtributo(String chave, String valor) {
        if (chave != null && valor != null) {
            this.atributos.put(chave.toLowerCase().trim(), valor.trim());
        }
    }

    /**
     * Remove um atributo do perfil.
     */
    public void removerAtributo(String chave) {
        if (chave != null) {
            this.atributos.remove(chave.toLowerCase().trim());
        }
    }

    /**
     * Verifica se os requisitos de um anúncio correspondem ao perfil do utilizador.
     */
    public boolean verificarCorrespondencia(Map<String, String> requisitos) {
        if (requisitos == null || requisitos.isEmpty()) return true;

        for (Map.Entry<String, String> entry : requisitos.entrySet()) {
            String valorPerfil = atributos.get(entry.getKey().toLowerCase().trim());
            if (valorPerfil == null || !valorPerfil.equalsIgnoreCase(entry.getValue().trim())) {
                return false;
            }
        }
        return true;
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

    public Map<String, String> getAtributos() { return atributos; }
    public void setAtributos(Map<String, String> atributos) { this.atributos = atributos; }

    public Long getTotalAnuncios() { return totalAnuncios; }
    public void setTotalAnuncios(Long totalAnuncios) { this.totalAnuncios = totalAnuncios; }

    public Long getTotalEntregas() { return totalEntregas; }
    public void setTotalEntregas(Long totalEntregas) { this.totalEntregas = totalEntregas; }
}
