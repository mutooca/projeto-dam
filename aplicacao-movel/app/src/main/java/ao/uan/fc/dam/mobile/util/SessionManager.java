package ao.uan.fc.dam.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF = "sessao";

    private static final String KEY_ID = "id_utilizador";
    private static final String KEY_NOME = "nome";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
        );

    }

    public void iniciarSessao(int id, String nome, String email) {

        preferences.edit()
                .putInt(KEY_ID, id)
                .putString(KEY_NOME, nome)
                .putString(KEY_EMAIL, email)
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

    public void terminarSessao() {

        preferences.edit().clear().apply();

    }

}