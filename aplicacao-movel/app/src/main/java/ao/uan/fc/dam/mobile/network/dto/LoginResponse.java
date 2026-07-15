package ao.uan.fc.dam.mobile.network.dto;

public class LoginResponse {

    private boolean success;
    private String ticket;
    private String sessionKey;
    private String sessionId;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getTicket() {
        return ticket;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessage() {
        return message;
    }
}
