package ao.uan.fc.dam.mobile.security;

import android.util.Base64;

import java.util.UUID;

public class KerberosManager {

    /**
     * Gera um autenticador Kerberos para uma requisição
     * @param email Email do utilizador
     * @param sessionKey Chave de sessão (em Base64)
     * @return Autenticador em Base64
     */
    public static String generateAuthenticator(String email, String sessionKey) {
        if (email == null || sessionKey == null || email.isEmpty() || sessionKey.isEmpty()) {
            return null;
        }

        try {
            // 1. Gerar timestamp atual (usando o KerberosCryptoUtil)
            String timestamp = KerberosCryptoUtil.getCurrentTimestamp();

            // 2. Gerar nonce único
            String nonce = UUID.randomUUID().toString();

            // 3. Dados para MAC: email|timestamp|nonce
            String data = email + "|" + timestamp + "|" + nonce;

            // 4. Calcular MAC (HMAC-SHA256) usando o KerberosCryptoUtil
            String mac = KerberosCryptoUtil.calculateMAC(data, sessionKey);

            // 5. Montar autenticador: email|timestamp|nonce|mac
            String authenticatorData = email + "|" + timestamp + "|" + nonce + "|" + mac;

            // 6. Codificar em Base64
            return Base64.encodeToString(authenticatorData.getBytes("UTF-8"), Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}