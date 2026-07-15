package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;

import ao.uan.fc.dam.mobile.data.dao.UtilizadorDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.request.RegistarUtilizadorRequest;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilizadorRepository {

    private final UtilizadorDao utilizadorDao;
    private final Context context;


    public UtilizadorRepository(Context context){
        this.context = context;
        utilizadorDao = DatabaseProvider
                .getInstance(context)
                .utilizadorDao();
    }


    // ============================
    // REGISTO NO SERVIDOR
    // ============================

    public void registarRemoto(Utilizador utilizador, ResultadoCallback<Utilizador> callback) {
        RegistarUtilizadorRequest request =
                new RegistarUtilizadorRequest(utilizador.getNome(), utilizador.getEmail(), utilizador.getPalavraChave());


        RetrofitClient.getApiService().registar(request)
                .enqueue(new Callback<Utilizador>() {

                    @Override
                    public void onResponse(Call<Utilizador> call,
                                           Response<Utilizador> response) {

                        if(response.isSuccessful()
                                && response.body()!=null){

                            // Guarda também localmente
                            inserirLocal(response.body(), id ->
                                    callback.onResultado(response.body())
                            );

                        }else{
                            callback.onResultado(null);
                        }
                    }


                    @Override
                    public void onFailure(Call<Utilizador> call,
                                          Throwable t) {

                        Log.e("API",
                                "Erro ao registar utilizador",
                                t);

                        callback.onResultado(null);
                    }
                });
    }



    // ============================
    // LOGIN NO SERVIDOR
    // ============================

    public void autenticarRemoto(String email,
                                 String senha,
                                 ResultadoCallback<Utilizador> callback){


        Utilizador dadosLogin = new Utilizador();

        dadosLogin.setEmail(email);
        dadosLogin.setPalavraChave(senha);


        RetrofitClient.getApiService()
                .login(dadosLogin)
                .enqueue(new Callback<Utilizador>() {


                    @Override
                    public void onResponse(Call<Utilizador> call,
                                           Response<Utilizador> response) {


                        if(response.isSuccessful()
                                && response.body()!=null){


                            Utilizador user = response.body();


                            // Guarda cache local
                            inserirLocal(user,
                                    id -> callback.onResultado(user));


                        }else{

                            callback.onResultado(null);
                        }

                    }


                    @Override
                    public void onFailure(Call<Utilizador> call,
                                          Throwable t) {


                        Log.e("API",
                                "Erro autenticação remota",
                                t);


                        // fallback offline
                        autenticarLocal(
                                email,
                                senha,
                                callback
                        );
                    }
                });
    }



    // ============================
    // ATUALIZAR PERFIL NO SERVIDOR
    // ============================

    public void atualizarRemoto(Utilizador utilizador,
                                ResultadoCallback<Utilizador> callback){


        RetrofitClient.getApiService()
                .atualizar(
                        utilizador.getIdUtilizador(),
                        utilizador
                )
                .enqueue(new Callback<Utilizador>() {


                    @Override
                    public void onResponse(Call<Utilizador> call,
                                           Response<Utilizador> response) {


                        if(response.isSuccessful()
                                && response.body()!=null){


                            Utilizador atualizado =
                                    response.body();


                            // Atualiza cache local
                            atualizarLocal(
                                    atualizado,
                                    resultado ->
                                            callback.onResultado(atualizado)
                            );


                        }else{

                            callback.onResultado(null);

                        }

                    }



                    @Override
                    public void onFailure(Call<Utilizador> call,
                                          Throwable t) {


                        Log.e("API",
                                "Erro ao atualizar utilizador",
                                t);


                        callback.onResultado(null);

                    }
                });
    }




    // ============================
    // ROOM - INSERIR
    // ============================

    private void inserirLocal(Utilizador utilizador,
                              ResultadoCallback<Long> callback){


        DatabaseExecutor.executor.execute(() -> {

            long id = utilizadorDao.inserir(utilizador);

            if(callback!=null)
                callback.onResultado(id);

        });
    }




    // ============================
    // ROOM - LOGIN OFFLINE
    // ============================

    public void autenticarLocal(String email,
                                String palavraChave,
                                ResultadoCallback<Utilizador> callback){


        DatabaseExecutor.executor.execute(() -> {

            Utilizador utilizador =
                    utilizadorDao.autenticar(
                            email,
                            palavraChave
                    );


            callback.onResultado(utilizador);

        });
    }




    // ============================
    // ROOM - BUSCAR POR ID
    // ============================

    public void buscarPorId(int id,
                            ResultadoCallback<Utilizador> callback){


        DatabaseExecutor.executor.execute(() -> {


            Utilizador utilizador =
                    utilizadorDao.buscarPorId(id);


            callback.onResultado(utilizador);

        });

    }




    // ============================
    // ROOM - ATUALIZAR CACHE
    // ============================

    public void atualizarLocal(Utilizador utilizador,
                               ResultadoCallback<Void> callback){


        DatabaseExecutor.executor.execute(() -> {


            utilizadorDao.atualizar(utilizador);


            if(callback!=null)
                callback.onResultado(null);

        });

    }

}