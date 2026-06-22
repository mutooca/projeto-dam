package ao.uan.fc.dam.mobile.model;

public class TicketResponse {
    private String message;
    private String sessionId;
    private String sessionKey;
    private boolean success;
    private String ticket;

    public boolean isSuccess() {
        return success || sessionId != null || ticket != null;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getTicket() {
        return ticket;
    }
}
