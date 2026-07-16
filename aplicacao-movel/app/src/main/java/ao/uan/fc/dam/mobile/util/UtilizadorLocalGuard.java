package ao.uan.fc.dam.mobile.util;

import android.content.Context;
import android.util.Log;

import java.time.LocalDateTime;

import ao.uan.fc.dam.mobile.data.dao.UtilizadorDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;

/**
 * Anuncio, AtributoPerfil e Historico têm todos uma foreign key para "utilizadores" (Room).
 * O id guardado no SessionManager (SharedPreferences) sobrevive a uma migração destrutiva
 * da base de dados Room (ex: bump de "version" sem o utilizador desinstalar a app) ou a
 * qualquer outro cenário em que a tabela local seja recriada sem sessão terminada — nesses
 * casos o id da sessão passa a apontar para uma linha que já não existe, e qualquer insert
 * com essa foreign key falha com SQLiteConstraintException (FOREIGN KEY constraint failed),
 * derrubando a app.
 * <p>
 * Chamar {@link #garantir(Context, SessionManager)} (numa thread de BD) antes de qualquer
 * insert que dependa dessa foreign key repõe a linha em falta usando os dados já em cache
 * no SessionManager, para que o insert seguinte tenha sempre uma foreign key válida.
 */
public final class UtilizadorLocalGuard {

    private static final String TAG = "UtilizadorLocalGuard";

    private UtilizadorLocalGuard() {
    }

    public static void garantir(Context context, SessionManager sessionManager) {
        int idSessao = sessionManager.getIdUtilizador();
        if (idSessao <= 0) {
            return;
        }

        UtilizadorDao dao = DatabaseProvider.getInstance(context).utilizadorDao();
        if (dao.buscarPorId(idSessao) != null) {
            return;
        }

        String email = sessionManager.getEmail();
        Utilizador porEmail = (email != null && !email.isBlank()) ? dao.buscarPorEmail(email) : null;

        if (porEmail != null) {
            // A linha existe mas com outro id local (sessão desatualizada face à BD actual).
            // Corrigir a sessão em vez de inserir um novo registo, que violaria o email único.
            Log.w(TAG, "Id da sessão (" + idSessao + ") não corresponde ao id local ("
                    + porEmail.getIdUtilizador() + ") para " + email + ". A corrigir sessão.");
            sessionManager.iniciarSessao(porEmail.getIdUtilizador(), porEmail.getNome(), porEmail.getEmail());
            return;
        }

        Log.w(TAG, "Utilizador local id=" + idSessao + " (email=" + email
                + ") não existe na BD - a repor para evitar violação de foreign key.");

        Utilizador reparado = new Utilizador();
        reparado.setIdUtilizador(idSessao);
        reparado.setNome(sessionManager.getNome());
        reparado.setEmail(email);
        reparado.setPalavraChave("");
        reparado.setSaldo(0);
        reparado.setDataCriacao(LocalDateTime.now());

        try {
            dao.inserir(reparado);
        } catch (Exception e) {
            Log.e(TAG, "Falha ao repor utilizador local id=" + idSessao, e);
        }
    }
}
