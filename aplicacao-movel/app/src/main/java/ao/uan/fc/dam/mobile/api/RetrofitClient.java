package ao.uan.fc.dam.mobile.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import ao.uan.fc.dam.mobile.BuildConfig;
import ao.uan.fc.dam.mobile.security.KerberosCryptoUtil;
import ao.uan.fc.dam.mobile.security.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private final ApiService api;

    private RetrofitClient(Context context) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_DATE))
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    
                    String ticket = SessionManager.getTicket(context);
                    String sessionKey = SessionManager.getSessionKey(context);
                    String email = SessionManager.getEmail(context);

                    if (ticket != null && sessionKey != null && email != null) {
                        String timestamp = KerberosCryptoUtil.getCurrentTimestamp();
                        String nonce = UUID.randomUUID().toString();
                        
                        String dataForMAC = email + "|" + timestamp + "|" + nonce;
                        String mac = KerberosCryptoUtil.calculateMAC(dataForMAC, sessionKey);
                        
                        String authRaw = email + "|" + timestamp + "|" + nonce + "|" + mac;
                        String authenticator = Base64.encodeToString(authRaw.getBytes(), Base64.NO_WRAP);
                        
                        builder.header("X-Kerberos-Ticket", ticket);
                        builder.header("X-Kerberos-Authenticator", authenticator);
                        Log.d("RetrofitClient", "Headers Kerberos injetados para: " + original.url().encodedPath());
                    }

                    return chain.proceed(builder.build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(ApiService.class);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext());
        }
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RetrofitClient.init(context) deve ser chamado antes de usar a API.");
        }
        return instance;
    }

    public ApiService getApi() {
        return api;
    }
}
