package ao.uan.fc.dam.mobile.network;

import java.util.Map;

public class ProfileMatcher {
    public static boolean aceitar(String politica, String restricao, Map<String, String> perfil) {

        if (restricao == null || restricao.trim().isEmpty()) {
            return true;
        }

        String[] partes = restricao.split("=");

        if (partes.length != 2) {
            return true;
        }

        String chave = partes[0].trim();
        String valor = partes[1].trim();

        String valorPerfil = perfil.get(chave);

        if (valorPerfil == null) {
            return politica.equalsIgnoreCase("BLACKLIST");
        }

        boolean igual = valor.equalsIgnoreCase(valorPerfil);

        if ("WHITELIST".equalsIgnoreCase(politica)) {
            return igual;
        }

        return !igual;
    }

}
