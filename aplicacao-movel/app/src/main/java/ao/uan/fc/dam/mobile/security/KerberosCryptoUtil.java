package ao.uan.fc.dam.mobile.security;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class KerberosCryptoUtil {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String calculateMAC(String message, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] macBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(macBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular MAC", e);
        }
    }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
