package ao.uan.fc.dam.mobile.network.api;

import java.util.List;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import ao.uan.fc.dam.mobile.request.RegistarUtilizadorRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiService {

    @POST("/api/auth/login")
    Call<Utilizador> login(@Body Utilizador utilizador);

    @PUT("api/utilizadores/{id}")
    Call<Utilizador> atualizar(@Path("id") int id, @Body Utilizador utilizador);

    @POST("api/auth/registar")
    Call<Utilizador> registar(@Body RegistarUtilizadorRequest request);
    @GET("api/anuncios")
    Call<List<Anuncio>> listarAnuncios();

    @POST("api/anuncios")
    Call<Anuncio> publicarAnuncio(@Body Anuncio anuncio);

    @GET("/api/infraestruturas/locais/todos")
    Call<List<Local>> listarLocais();

    @GET("api/anuncios/local/{id}")
    Call<List<Anuncio>> listarAnunciosPorLocal(@Path("id") int idLocal);
}
