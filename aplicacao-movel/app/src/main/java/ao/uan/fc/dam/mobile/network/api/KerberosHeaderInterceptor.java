package ao.uan.fc.dam.mobile.network.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ao.uan.fc.dam.mobile.network.dto.AuthenticatorRequest;
import ao.uan.fc.dam.mobile.network.dto.AuthenticatorResponse;
import ao.uan.fc.dam.mobile.util.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class KerberosHeaderInterceptor implements Interceptor {

    private static final String TAG = "KerberosHeaders";
    private final SessionManager sessionManager;

    public KerberosHeaderInterceptor(Context context) {
        this.sessionManager = new SessionManager(context.getApplicationContext());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        if (!requiresKerberos(path)) {
            Log.d(TAG, "Rota publica, sem headers Kerberos: " + path);
            return chain.proceed(originalRequest);
        }

        if (!sessionManager.hasKerberosSession()) {
            Log.w(TAG, "Sessao Kerberos ausente para " + path
                    + " email=" + sessionManager.getEmail()
                    + " sessionId=" + sessionManager.getSessionId());
            return chain.proceed(originalRequest);
        }

        Log.d(TAG, "A gerar authenticator para " + path
                + " email=" + sessionManager.getEmail()
                + " sessionId=" + sessionManager.getSessionId());

        String authenticator = fetchAuthenticator();
        if (authenticator == null || authenticator.isBlank()) {
            Log.w(TAG, "Nao foi possivel gerar authenticator para " + path);
            return chain.proceed(originalRequest);
        }

        Request authenticatedRequest = originalRequest.newBuilder()
                .header("X-Kerberos-Ticket", sessionManager.getTicket())
                .header("X-Kerberos-Authenticator", authenticator)
                .build();

        Log.d(TAG, "Headers Kerberos aplicados em " + path
                + " ticketPrefix=" + resumir(sessionManager.getTicket())
                + " authenticatorPrefix=" + resumir(authenticator));

        return chain.proceed(authenticatedRequest);
    }

    private boolean requiresKerberos(String path) {
        return path.startsWith("/api/") && !isPublicRoute(path);
    }

    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/infraestruturas/listar-todas")
                || path.startsWith("/api/infraestruturas/estatisticas")
                || path.startsWith("/api/infraestruturas/proximas")
                || path.startsWith("/api/kerberos/");
    }

    private String fetchAuthenticator() {
        try {
            retrofit2.Response<AuthenticatorResponse> response = KerberosRetrofitClient.getApiService()
                    .createAuthenticator(new AuthenticatorRequest(
                            sessionManager.getSessionId(),
                            sessionManager.getEmail()
                    ))
                    .execute();

            if (!response.isSuccessful() || response.body() == null) {
                Log.e(TAG, "Falha ao gerar authenticator: HTTP " + response.code());
                return null;
            }

            String authenticator = response.body().getAuthenticator();
            sessionManager.atualizarAuthenticator(authenticator);
            return authenticator;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao gerar authenticator", e);
            return null;
        }
    }

    private String resumir(String valor) {
        if (valor == null || valor.isBlank()) {
            return "vazio";
        }
        int tamanho = Math.min(12, valor.length());
        return valor.substring(0, tamanho) + "...";
    }
}
