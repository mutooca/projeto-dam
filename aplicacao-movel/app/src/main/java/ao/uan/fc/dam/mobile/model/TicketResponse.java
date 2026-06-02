package ao.uan.fc.dam.mobile.model;


public class TicketResponse {
    private String message;
    private String sessionId;
    private String sessionKey;
    private boolean success;
    private String ticket;

    public boolean isSuccess() {
        return this.success;
    }

    public String getTicket() {
        return this.ticket;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getMessage() {
        return this.message;
    }
}