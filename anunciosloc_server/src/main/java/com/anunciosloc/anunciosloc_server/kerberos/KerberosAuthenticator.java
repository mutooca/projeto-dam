package com.anunciosloc.anunciosloc_server.kerberos;

import lombok.Builder;
import lombok.Data;
import java.util.Base64;

@Data
@Builder
public class KerberosAuthenticator {
    private String emailUtilizador;
    private String timestamp;
    private String nonce;
    private String mac;  // MAC calculado com chave de sessão
    
    public String serialize() {
        return Base64.getEncoder().encodeToString(
            (emailUtilizador + "|" + timestamp + "|" + nonce + "|" + mac).getBytes()
        );
    }
    
    public static KerberosAuthenticator deserialize(String data) {
        String decoded = new String(Base64.getDecoder().decode(data));
        String[] parts = decoded.split("\\|");
        return KerberosAuthenticator.builder()
            .emailUtilizador(parts[0])
            .timestamp(parts[1])
            .nonce(parts[2])
            .mac(parts[3])
            .build();
    }
}
