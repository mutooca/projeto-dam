package ao.uan.fc.dam.mobile.security;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/* JADX INFO: loaded from: classes8.dex */
public class KerberosHeaderInterceptor implements Interceptor {
    private final Context context;

    public KerberosHeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        String path = original.url().encodedPath();
        if (path.contains("/api/auth/")) {
            return chain.proceed(original);
        }
        Request.Builder builder = original.newBuilder();
        String ticket = KerberosAuthManager.getTicket(this.context);
        String sessionKey = KerberosAuthManager.getSessionKey(this.context);
        String email = KerberosAuthManager.getEmail(this.context);
        if (ticket != null && sessionKey != null && email != null) {
            String authenticator = KerberosCryptoUtil.generateAuthenticator(email, sessionKey);
            builder.header("X-Kerberos-Ticket", ticket);
            builder.header("X-Kerberos-Authenticator", authenticator);
        }
        String sessionId = KerberosAuthManager.getSessionId(this.context);
        if (sessionId != null) {
            builder.header("X-Session-Id", sessionId);
        }
        return chain.proceed(builder.build());
    }
}