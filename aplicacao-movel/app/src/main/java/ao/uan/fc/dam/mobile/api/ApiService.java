package ao.uan.fc.dam.mobile.api;

import java.util.List;
import java.util.Map;

import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.TicketResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/login")
    Call<TicketResponse> login(@Body Map<String, String> request);

    @POST("api/auth/registar")
    Call<ResponseBody> registarUtilizador(@Body Map<String, String> request);

    @GET("api/anuncios/saldo/{email}")
    Call<Utilizador> obterSaldo(@Path("email") String email);

    @GET("api/anuncios/infraestruturas")
    Call<List<Local>> listarInfraestruturas(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("k") int quantidade
    );

    @POST("api/anuncios/infraestruturas")
    Call<Local> criarInfraestrutura(@Body Map<String, Object> request);

    @DELETE("api/anuncios/infraestruturas/{id}")
    Call<ResponseBody> removerInfraestrutura(@Path("id") Long id);

    @POST("api/anuncios/mensagem")
    Call<Anuncio> postarMensagem(@Body Map<String, Object> request);

    @GET("api/anuncios/local/{infraId}/mensagens")
    Call<List<Anuncio>> listarMensagensPorLocal(@Path("infraId") Long infraestruturaId);

    @GET("api/anuncios/minhas-mensagens/{email}")
    Call<List<Anuncio>> listarMinhasMensagens(@Path("email") String email);

    @GET("api/anuncios/historico/{email}")
    Call<List<Anuncio>> listarHistorico(@Path("email") String email);

    @DELETE("api/anuncios/{id}")
    Call<ResponseBody> removerAnuncio(@Path("id") Long id);

    @PUT("api/anuncios/perfil")
    Call<Utilizador> editarPerfil(@Body Map<String, Object> request);
}
