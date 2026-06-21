package ao.uan.fc.dam.mobile.api;

import ao.uan.fc.dam.mobile.model.Utilizador;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("utilizador")
    Call<Utilizador> buscarUtilizador();
}
