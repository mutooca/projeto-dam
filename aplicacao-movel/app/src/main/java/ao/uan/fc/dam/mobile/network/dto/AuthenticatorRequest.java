package ao.uan.fc.dam.mobile.network.dto;

public class AuthenticatorRequest {

    private final String sessionId;
    private final String email;

    public AuthenticatorRequest(String sessionId, String email) {
        this.sessionId = sessionId;
        this.email = email;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEmail() {
        return email;
    }
}
