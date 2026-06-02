package ao.uan.fc.dam.mobile.security;

import android.content.Context;
import android.content.SharedPreferences;
import ao.uan.fc.dam.mobile.model.TicketResponse;

public class KerberosAuthManager {
    private static final String PREFS = "kerberos_session";
    private static final String USER_PREFS = "user_prefs";

    private KerberosAuthManager() {
    }

    public static void saveSession(Context context, String email, TicketResponse response) {
        SharedPreferences.Editor session = context.getSharedPreferences(PREFS, 0).edit();
        session.putString("email", email);
        session.putString("ticket", response.getTicket());
        session.putString("sessionKey", response.getSessionKey());
        session.putString("sessionId", response.getSessionId());
        session.apply();
        context.getSharedPreferences(USER_PREFS, 0).edit().putString("email", email).apply();
    }

    public static String getEmail(Context context) {
        String email = context.getSharedPreferences(PREFS, 0).getString("email", null);
        if (email != null) {
            return email;
        }
        return context.getSharedPreferences(USER_PREFS, 0).getString("email", null);
    }

    public static String getTicket(Context context) {
        return context.getSharedPreferences(PREFS, 0).getString("ticket", null);
    }

    public static String getSessionKey(Context context) {
        return context.getSharedPreferences(PREFS, 0).getString("sessionKey", null);
    }

    public static String getSessionId(Context context) {
        return context.getSharedPreferences(PREFS, 0).getString("sessionId", null);
    }

    public static boolean hasTicket(Context context) {
        return getTicket(context) != null;
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREFS, 0).edit().clear().apply();
        context.getSharedPreferences(USER_PREFS, 0).edit().clear().apply();
    }
}