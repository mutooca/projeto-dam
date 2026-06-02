package ao.uan.fc.dam.mobile.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ao.uan.fc.dam.mobile.security.KerberosHeaderInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.62.229:8080/";
    private static RetrofitClient instance;
    private final ApiService api;

    private RetrofitClient(Context context) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                                new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE_TIME)))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_DATE))
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new KerberosHeaderInterceptor(context.getApplicationContext()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(ApiService.class);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
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
