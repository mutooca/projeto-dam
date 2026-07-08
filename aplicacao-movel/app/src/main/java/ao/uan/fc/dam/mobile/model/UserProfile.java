package ao.uan.fc.dam.mobile.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa o perfil de um utilizador composto por pares chave-valor.
 * Utilizado para filtragem de anúncios na rede P2P.
 */
public class UserProfile {
    private Map<String, String> attributes;

    public UserProfile() {
        this.attributes = new HashMap<>();
    }

    public UserProfile(Map<String, String> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    /**
     * Adiciona ou atualiza um atributo no perfil.
     */
    public void addAttribute(String key, String value) {
        if (key != null && value != null) {
            attributes.put(key.toLowerCase().trim(), value.trim());
        }
    }

    /**
     * Remove um atributo do perfil.
     */
    public void removeAttribute(String key) {
        if (key != null) {
            attributes.remove(key.toLowerCase().trim());
        }
    }

    /**
     * Verifica se os requisitos de um anúncio correspondem a este perfil.
     * O critério é: para cada chave no requisito, o perfil deve ter o mesmo valor.
     * Exemplo: Requisito {club: "Real Madrid"} match com Perfil {club: "Real Madrid", profissao: "Estudante"}
     * 
     * @param requirements Mapa de atributos exigidos.
     * @return true se corresponder.
     */
    public boolean matches(Map<String, String> requirements) {
        if (requirements == null || requirements.isEmpty()) return true;

        for (Map.Entry<String, String> entry : requirements.entrySet()) {
            String profileValue = attributes.get(entry.getKey().toLowerCase().trim());
            if (profileValue == null || !profileValue.equalsIgnoreCase(entry.getValue().trim())) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
