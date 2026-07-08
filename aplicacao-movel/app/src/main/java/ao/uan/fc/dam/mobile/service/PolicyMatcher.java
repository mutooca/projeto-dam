package ao.uan.fc.dam.mobile.service;

import java.util.Map;

/**
 * Utilitário para validação de políticas de filtragem (Whitelist/Blacklist)
 * baseadas nos atributos de perfil dos utilizadores.
 */
public class PolicyMatcher {

    /**
     * Verifica se um perfil satisfaz as condições de Whitelist e Blacklist.
     * 
     * @param profile Atributos do utilizador (ex: {club: "Real Madrid", role: "Estudante"})
     * @param whitelist Atributos obrigatórios (O utilizador deve ter todos estes)
     * @param blacklist Atributos proibidos (O utilizador não pode ter nenhum destes)
     * @return true se o perfil for aceite pela política.
     */
    public static boolean match(Map<String, String> profile, Map<String, String> whitelist, Map<String, String> blacklist) {
        if (profile == null) return (whitelist == null || whitelist.isEmpty());

        // 1. Verificar Blacklist (Prioridade: Se bater em qualquer regra, é rejeitado)
        if (blacklist != null && !blacklist.isEmpty()) {
            for (Map.Entry<String, String> entry : blacklist.entrySet()) {
                String key = entry.getKey().toLowerCase().trim();
                String value = entry.getValue().trim();
                
                if (profile.containsKey(key)) {
                    if (profile.get(key).equalsIgnoreCase(value)) {
                        return false; // Encontrou um atributo proibido
                    }
                }
            }
        }

        // 2. Verificar Whitelist (Se definida, o perfil deve cumprir TODAS as exigências)
        if (whitelist != null && !whitelist.isEmpty()) {
            for (Map.Entry<String, String> entry : whitelist.entrySet()) {
                String key = entry.getKey().toLowerCase().trim();
                String value = entry.getValue().trim();
                
                String profileValue = profile.get(key);
                if (profileValue == null || !profileValue.equalsIgnoreCase(value)) {
                    return false; // Falta um atributo obrigatório ou o valor é diferente
                }
            }
        }

        return true; // Passou em todas as verificações
    }
}
