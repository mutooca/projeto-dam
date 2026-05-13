package com.anunciosloc.anunciosloc_server.kerberos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
@Builder
public class KerberosTicket {
    private String sessionId;
    private String emailUtilizador;
    private String sessionKey;  // Criptografado com chave do utilizador
    private LocalDateTime criadoEm;
    private LocalDateTime expiraEm;
    private String serviceName;  // "AnunciosLoc"
    
    // Serializar para string (para transporte)
    // Serialize
    public String serialize() {
        String data = sessionId + "|" + emailUtilizador + "|" + sessionKey + "|" + 
                    criadoEm + "|" + expiraEm + "|" + serviceName;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    
        // Deserialize
    public static KerberosTicket deserialize(String data) {
        String decoded = new String(Base64.getDecoder().decode(data));
        String[] parts = decoded.split("\\|");
        return KerberosTicket.builder()
            .sessionId(parts[0])
            .emailUtilizador(parts[1])
            .sessionKey(parts[2])
            .criadoEm(LocalDateTime.parse(parts[3]))
            .expiraEm(LocalDateTime.parse(parts[4]))
            .serviceName(parts[5])
            .build();
    }
}

