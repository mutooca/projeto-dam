package ao.uan.fc.dam.mobile.network.dto;

public class LogoutRequest {

    private final String sessionId;

    public LogoutRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
