package ao.uan.fc.dam.mobile.api;

import java.util.List;
import java.util.Map;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.SaldoResponse;
import ao.uan.fc.dam.mobile.model.TicketResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/login")
    Call<TicketResponse> login(@Body Map<String, String> request);

    @POST("api/auth/registar")
    Call<ResponseBody> registarUtilizador(@Body Map<String, String> request);

    @POST("api/auth/logout")
    Call<ResponseBody> logout(@Body Map<String, String> request);

    @GET("api/locais")
    Call<List<Local>> listarLocais(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude
    );

    @POST("api/locais")
    Call<Local> criarLocal(@Body Map<String, Object> request);

    @DELETE("api/locais/{id}")
    Call<ResponseBody> removerLocal(@Path("id") String id, @Query("email") String email);

    @GET("api/infraestruturas/proximas")
    Call<List<Map<String, Object>>> listarInfraestruturasProximas(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("k") int k
    );

    @POST("api/anuncios")
    Call<Anuncio> postarAnuncio(@Body Map<String, Object> request);

    @GET("api/anuncios/utilizador/{email}")
    Call<List<Anuncio>> listarMinhasMensagens(@Path("email") String email);

    @GET("api/anuncios/local/{localId}")
    Call<List<Anuncio>> listarPorLocal(@Path("localId") String localId);

    @GET("api/anuncios/receber/{email}/{infraId}")
    Call<List<Anuncio>> receberAnuncios(
            @Path("email") String email,
            @Path("infraId") String infra_id,
            @Query("lat") Double latitude,
            @Query("lon") Double longitude
    );

    @POST("api/anuncios/anunciar-localizacao")
    Call<List<Anuncio>> anunciarLocalizacao(@Body Map<String, Object> syncData);

    @DELETE("api/anuncios/{id}")
    Call<ResponseBody> removerAnuncio(@Path("id") String id, @Query("email") String email);

    @PATCH("api/anuncios/{id}/lido")
    Call<ResponseBody> marcarComoLido(@Path("id") String id, @Query("email") String email);

    @GET("api/utilizadores/{email}/saldo")
    Call<SaldoResponse> obterSaldo(@Path("email") String email);

    @PUT("api/utilizadores/{email}/preferencias")
    Call<ResponseBody> atualizarPreferencias(
            @Path("email") String email,
            @Query("preferencias") String preferencias
    );

    @PUT("api/utilizadores/perfil")
    Call<Utilizador> editarPerfil(@Body Map<String, Object> request);

    @GET("api/utilizadores/chaves-perfil")
    Call<List<String>> listarChavesPerfil();
}
