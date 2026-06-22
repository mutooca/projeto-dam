package ao.uan.fc.dam.mobile.security;

import android.content.Context;
import android.content.SharedPreferences;

import ao.uan.fc.dam.mobile.model.TicketResponse;

public final class SessionManager {
    private static final String PREFS = "anunciosloc_session";

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void save(Context context, String email, TicketResponse response) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("email", email);
        if (response != null) {
            editor.putString("sessionId", response.getSessionId());
            editor.putString("ticket", response.getTicket());
            editor.putString("sessionKey", response.getSessionKey());
        }
        editor.commit();
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString("email", null);
    }

    public static String getSessionId(Context context) {
        return getPrefs(context).getString("sessionId", null);
    }

    public static String getTicket(Context context) {
        return getPrefs(context).getString("ticket", null);
    }

    public static String getSessionKey(Context context) {
        return getPrefs(context).getString("sessionKey", null);
    }

    public static boolean hasSession(Context context) {
        return getEmail(context) != null && getSessionId(context) != null;
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().commit();
    }
}
