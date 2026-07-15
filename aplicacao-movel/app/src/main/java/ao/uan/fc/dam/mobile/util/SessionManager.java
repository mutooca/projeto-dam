package ao.uan.fc.dam.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF = "sessao";

    private static final String KEY_ID = "id_utilizador";
    private static final String KEY_NOME = "nome";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TICKET = "ticket";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_SESSION_KEY = "session_key";
    private static final String KEY_AUTHENTICATOR = "authenticator";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
        );

    }

    public void iniciarSessao(int id, String nome, String email) {
        String existingEmail = preferences.getString(KEY_EMAIL, "");
        String ticket = "";
        String sessionId = "";
        String sessionKey = "";
        String authenticator = "";

        if (email != null && email.equals(existingEmail)) {
            ticket = getTicket();
            sessionId = getSessionId();
            sessionKey = getSessionKey();
            authenticator = getAuthenticator();
        }

        preferences.edit()
                .putInt(KEY_ID, id)
                .putString(KEY_NOME, nome)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TICKET, ticket)
                .putString(KEY_SESSION_ID, sessionId)
                .putString(KEY_SESSION_KEY, sessionKey)
                .putString(KEY_AUTHENTICATOR, authenticator)
                .apply();

    }

    public void iniciarSessao(
            int id,
            String nome,
            String email,
            String ticket,
            String sessionId,
            String sessionKey
    ) {
        preferences.edit()
                .putInt(KEY_ID, id)
                .putString(KEY_NOME, nome)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TICKET, ticket)
                .putString(KEY_SESSION_ID, sessionId)
                .putString(KEY_SESSION_KEY, sessionKey)
                .remove(KEY_AUTHENTICATOR)
                .apply();
    }

    public void atualizarAuthenticator(String authenticator) {
        preferences.edit()
                .putString(KEY_AUTHENTICATOR, authenticator)
                .apply();
    }

    public boolean estaAutenticado() {

        return preferences.contains(KEY_ID);

    }

    public int getIdUtilizador() {

        return preferences.getInt(KEY_ID, -1);

    }

    public String getNome() {

        return preferences.getString(KEY_NOME, "");

    }

    public String getEmail() {

        return preferences.getString(KEY_EMAIL, "");

    }

    public String getTicket() {
        return preferences.getString(KEY_TICKET, "");
    }

    public String getSessionId() {
        return preferences.getString(KEY_SESSION_ID, "");
    }

    public String getSessionKey() {
        return preferences.getString(KEY_SESSION_KEY, "");
    }

    public String getAuthenticator() {
        return preferences.getString(KEY_AUTHENTICATOR, "");
    }

    public boolean hasKerberosSession() {
        return !getEmail().isBlank()
                && !getTicket().isBlank()
                && !getSessionId().isBlank();
    }

    public void terminarSessao() {

        preferences.edit().clear().apply();

    }

}
