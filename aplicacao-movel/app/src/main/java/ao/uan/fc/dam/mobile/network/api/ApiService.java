package ao.uan.fc.dam.mobile.network.api;

import java.util.List;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import ao.uan.fc.dam.mobile.network.dto.AtualizarUtilizadorRequest;
import ao.uan.fc.dam.mobile.network.dto.CriarLocalRequest;
import ao.uan.fc.dam.mobile.network.dto.CriarLocalResponse;
import ao.uan.fc.dam.mobile.network.dto.LoginRequest;
import ao.uan.fc.dam.mobile.network.dto.LoginResponse;
import ao.uan.fc.dam.mobile.network.dto.LogoutRequest;
import ao.uan.fc.dam.mobile.request.RegistarUtilizadorRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @PUT("/api/utilizadores/atualizar")
    Call<String> atualizar(@Body AtualizarUtilizadorRequest request);

    @POST("api/auth/registar")
    Call<String> registar(@Body RegistarUtilizadorRequest request);

    @POST("/api/auth/logout")
    Call<String> logout(@Body LogoutRequest request);

    @GET("api/anuncios")
    Call<List<Anuncio>> listarAnuncios();

    @POST("api/anuncios")
    Call<Anuncio> publicarAnuncio(@Body Anuncio anuncio);

    @GET("/api/infraestruturas/locais/todos")
    Call<List<Local>> listarLocais();

    @POST("/api/locais/criar")
    Call<CriarLocalResponse> criarLocal(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Body CriarLocalRequest request
    );

    @GET("api/anuncios/local/{id}")
    Call<List<Anuncio>> listarAnunciosPorLocal(@Path("id") int idLocal);
}
