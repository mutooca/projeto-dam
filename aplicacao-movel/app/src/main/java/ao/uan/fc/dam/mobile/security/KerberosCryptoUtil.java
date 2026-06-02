package ao.uan.fc.dam.mobile.security;

import android.util.Base64;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes8.dex */
public class KerberosCryptoUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String calculateMAC(String message, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] macBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(macBytes, 2);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular MAC", e);
        }
    }

    public static String generateAuthenticator(String email, String sessionKey) {
        String timestamp = LocalDateTime.now().toString();
        String nonce = UUID.randomUUID().toString();
        String dataForMAC = email + "|" + timestamp + "|" + nonce;
        String mac = calculateMAC(dataForMAC, sessionKey);
        String authData = email + "|" + timestamp + "|" + nonce + "|" + mac;
        return Base64.encodeToString(authData.getBytes(StandardCharsets.UTF_8), 2);
    }
}