package ao.uan.fc.dam.mobile.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileMatcher {
    public static boolean aceitar(String politica, String restricao, Map<String, String> perfil) {
        if (restricao == null || restricao.trim().isEmpty()) {
            return true;
        }

        List<String[]> restricoes = parseRestricoes(restricao);
        if (restricoes.isEmpty()) {
            return true;
        }

        boolean corresponde = false;
        for (String[] item : restricoes) {
            String valorPerfil = procurarValorPerfil(perfil, item[0]);
            if (valorPerfil != null && item[1].equalsIgnoreCase(valorPerfil.trim())) {
                corresponde = true;
                break;
            }
        }

        if ("WHITELIST".equalsIgnoreCase(politica)) {
            return corresponde;
        }

        if ("BLACKLIST".equalsIgnoreCase(politica)) {
            return !corresponde;
        }

        return true;
    }

    private static List<String[]> parseRestricoes(String restricao) {
        List<String[]> itens = new ArrayList<>();
        for (String par : restricao.split(",")) {
            String normalizado = par == null ? "" : par.trim();
            if (normalizado.isEmpty()) {
                continue;
            }

            String[] partes = normalizado.split("=", 2);
            if (partes.length != 2) {
                continue;
            }

            String chave = partes[0].trim();
            String valor = partes[1].trim();
            if (chave.isEmpty() || valor.isEmpty()) {
                continue;
            }

            itens.add(new String[]{
                    chave.toLowerCase(Locale.ROOT),
                    valor
            });
        }
        return itens;
    }

    private static String procurarValorPerfil(Map<String, String> perfil, String chaveNormalizada) {
        if (perfil == null || perfil.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> entry : perfil.entrySet()) {
            String chave = entry.getKey();
            if (chave != null && chave.trim().toLowerCase(Locale.ROOT).equals(chaveNormalizada)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
