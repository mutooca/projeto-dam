package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.Locale;

import ao.uan.fc.dam.mobile.BuildConfig;
import ao.uan.fc.dam.mobile.data.dao.UtilizadorDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.network.dto.AtualizarUtilizadorRequest;
import ao.uan.fc.dam.mobile.network.dto.LoginRequest;
import ao.uan.fc.dam.mobile.network.dto.LoginResponse;
import ao.uan.fc.dam.mobile.network.dto.LogoutRequest;
import ao.uan.fc.dam.mobile.request.RegistarUtilizadorRequest;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import ao.uan.fc.dam.mobile.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilizadorRepository {

    private static final String TAG = "UtilizadorRepository";

    private final UtilizadorDao utilizadorDao;
    private final Context context;
    private final SessionManager sessionManager;


    public UtilizadorRepository(Context context){
        this.context = context.getApplicationContext();
        utilizadorDao = DatabaseProvider
                .getInstance(this.context)
                .utilizadorDao();
        sessionManager = new SessionManager(this.context);
    }


    // ============================
    // REGISTO NO SERVIDOR
    // ============================

    public void registarRemoto(Utilizador utilizador, ResultadoCallback<Utilizador> callback) {
        RegistarUtilizadorRequest request =
                new RegistarUtilizadorRequest(utilizador.getNome(), utilizador.getEmail(), utilizador.getPalavraChave());


        RetrofitClient.getApiService(context).registar(request)
                .enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call,
                                           Response<String> response) {

                        if (response.isSuccessful()) {
                            DatabaseExecutor.executor.execute(() -> {
                                if (utilizador.getSaldo() == null) {
                                    utilizador.setSaldo(10);
                                }
                                if (utilizador.getDataCriacao() == null) {
                                    utilizador.setDataCriacao(LocalDateTime.now());
                                }
                                Utilizador persistido = salvarOuAtualizarLocal(utilizador);
                                callback.onResultado(persistido);
                            });

                        } else {
                            callback.onResultado(null);
                        }
                    }


                    @Override
                    public void onFailure(Call<String> call,
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
        LoginRequest dadosLogin = new LoginRequest(email, senha);

        RetrofitClient.getApiService(context)
                .login(dadosLogin)
                .enqueue(new Callback<LoginResponse>() {


                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        Log.d(TAG, "Resposta login remoto HTTP=" + response.code()
                                + " success=" + response.isSuccessful()
                                + " body=" + response.body());


                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {
                            persistirSessaoRemota(email, senha, response.body(), callback);
                        } else {
                            Log.e(TAG, "Login remoto rejeitado pelo servidor."
                                    + " endpoint=" + BuildConfig.API_BASE_URL
                                    + " errorBody=" + lerErro(response));

                            callback.onResultado(null);
                        }

                    }


                    @Override
                    public void onFailure(Call<LoginResponse> call,
                                          Throwable t) {


                        Log.e(TAG,
                                "Falha de ligacao no login remoto. endpoint="
                                        + BuildConfig.API_BASE_URL,
                                t);
                        callback.onResultado(null);
                    }
                });
    }



    // ============================
    // ATUALIZAR PERFIL NO SERVIDOR
    // ============================

    public void atualizarRemoto(Utilizador utilizador,
                                ResultadoCallback<Utilizador> callback){

        DatabaseExecutor.executor.execute(() -> {
            String emailSessao = sessionManager.getEmail();
            String emailActual = emailSessao.isBlank() ? utilizador.getEmail() : emailSessao;
            Utilizador utilizadorActual = utilizadorDao.buscarPorEmail(emailActual);
            String palavraChaveActual = utilizadorActual != null ? utilizadorActual.getPalavraChave() : "";

            String novaPalavraChave = null;
            if (utilizador.getPalavraChave() != null
                    && !utilizador.getPalavraChave().isBlank()
                    && !utilizador.getPalavraChave().equals(palavraChaveActual)) {
                novaPalavraChave = utilizador.getPalavraChave();
            }

            AtualizarUtilizadorRequest request = new AtualizarUtilizadorRequest(
                    emailActual,
                    utilizador.getNome(),
                    novaPalavraChave,
                    novaPalavraChave != null ? palavraChaveActual : null,
                    null
            );

            final Utilizador utilizadorBase = utilizadorActual;
            final String emailAtualFinal = emailActual;
            final String novaPalavraChaveFinal = novaPalavraChave;

            RetrofitClient.getApiService(context)
                    .atualizar(request)
                    .enqueue(new Callback<String>() {


                        @Override
                        public void onResponse(Call<String> call,
                                               Response<String> response) {


                            if (response.isSuccessful()) {
                                DatabaseExecutor.executor.execute(() -> {
                                    Utilizador atualizado = utilizadorBase != null ? utilizadorBase : new Utilizador();
                                    atualizado.setNome(utilizador.getNome());
                                    atualizado.setEmail(emailAtualFinal);

                                    if (novaPalavraChaveFinal != null) {
                                        atualizado.setPalavraChave(novaPalavraChaveFinal);
                                    } else if (atualizado.getPalavraChave() == null || atualizado.getPalavraChave().isBlank()) {
                                        atualizado.setPalavraChave(utilizador.getPalavraChave());
                                    }

                                    if (atualizado.getSaldo() == null) {
                                        atualizado.setSaldo(0);
                                    }

                                    Utilizador persistido = salvarOuAtualizarLocal(atualizado);
                                    sessionManager.iniciarSessao(
                                            persistido.getIdUtilizador(),
                                            persistido.getNome(),
                                            persistido.getEmail()
                                    );
                                    callback.onResultado(persistido);
                                });

                            } else {

                                callback.onResultado(null);

                            }

                        }



                        @Override
                        public void onFailure(Call<String> call,
                                              Throwable t) {


                            Log.e("API",
                                    "Erro ao atualizar utilizador",
                                    t);


                            callback.onResultado(null);

                        }
                    });
        });
    }

    public void terminarSessaoRemota(ResultadoCallback<Boolean> callback) {
        if (!sessionManager.hasKerberosSession()) {
            callback.onResultado(true);
            return;
        }

        RetrofitClient.getApiService(context)
                .logout(new LogoutRequest(sessionManager.getSessionId()))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        callback.onResultado(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("API", "Erro ao terminar sessao remota", t);
                        callback.onResultado(false);
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

    private void persistirSessaoRemota(
            String email,
            String senha,
            LoginResponse response,
            ResultadoCallback<Utilizador> callback
    ) {
        DatabaseExecutor.executor.execute(() -> {
            Utilizador utilizador = new Utilizador();
            utilizador.setNome(construirNomePadrao(email));
            utilizador.setEmail(email);
            utilizador.setPalavraChave(senha);
            utilizador.setSaldo(0);
            utilizador.setDataCriacao(LocalDateTime.now());

            Utilizador persistido = salvarOuAtualizarLocal(utilizador);

            sessionManager.iniciarSessao(
                    persistido.getIdUtilizador(),
                    persistido.getNome(),
                    persistido.getEmail(),
                    response.getTicket(),
                    response.getSessionId(),
                    response.getSessionKey()
            );

            callback.onResultado(persistido);
        });
    }

    private Utilizador salvarOuAtualizarLocal(Utilizador utilizador) {
        Utilizador existente = utilizadorDao.buscarPorEmail(utilizador.getEmail());

        if (existente == null) {
            if (utilizador.getNome() == null || utilizador.getNome().isBlank()) {
                utilizador.setNome(construirNomePadrao(utilizador.getEmail()));
            }
            long id = utilizadorDao.inserir(utilizador);
            utilizador.setIdUtilizador((int) id);
            return utilizador;
        }

        if (utilizador.getNome() != null && !utilizador.getNome().isBlank()) {
            existente.setNome(utilizador.getNome());
        } else if (existente.getNome() == null || existente.getNome().isBlank()) {
            existente.setNome(construirNomePadrao(existente.getEmail()));
        }

        existente.setEmail(utilizador.getEmail());

        if (utilizador.getPalavraChave() != null && !utilizador.getPalavraChave().isBlank()) {
            existente.setPalavraChave(utilizador.getPalavraChave());
        }

        if (utilizador.getSaldo() != null) {
            existente.setSaldo(utilizador.getSaldo());
        } else if (existente.getSaldo() == null) {
            existente.setSaldo(0);
        }

        if (utilizador.getDataCriacao() != null) {
            existente.setDataCriacao(utilizador.getDataCriacao());
        } else if (existente.getDataCriacao() == null) {
            existente.setDataCriacao(LocalDateTime.now());
        }

        utilizadorDao.atualizar(existente);
        return existente;
    }

    private String construirNomePadrao(String email) {
        if (email == null || email.isBlank()) {
            return "Utilizador";
        }

        String localPart = email.split("@")[0].replace('.', ' ').replace('_', ' ').trim();
        if (localPart.isEmpty()) {
            return "Utilizador";
        }

        String primeiraLetra = localPart.substring(0, 1).toUpperCase(Locale.ROOT);
        String resto = localPart.length() > 1 ? localPart.substring(1) : "";
        return primeiraLetra + resto;
    }

    private String lerErro(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return "sem corpo";
        }

        try {
            String erro = response.errorBody().string();
            return erro == null || erro.isBlank() ? "vazio" : erro;
        } catch (Exception e) {
            return "erro ao ler corpo: " + e.getMessage();
        }
    }

    /**
     * Saldo de pontos atual do utilizador (ganho quando outros abrem os seus anúncios).
     * Vai sempre ao servidor: o perfil deve mostrar o valor mais recente, não uma cópia local.
     */
    public void obterSaldoRemoto(
            String email,
            ResultadoCallback<Integer> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        RetrofitClient.getApiService(context)
                .consultarSaldo(email)
                .enqueue(new Callback<ao.uan.fc.dam.mobile.network.dto.SaldoResponseDto>() {
                    @Override
                    public void onResponse(
                            Call<ao.uan.fc.dam.mobile.network.dto.SaldoResponseDto> call,
                            Response<ao.uan.fc.dam.mobile.network.dto.SaldoResponseDto> response
                    ) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "Falha ao consultar saldo: HTTP " + response.code() + " " + lerErro(response));
                            if (errorCallback != null) {
                                errorCallback.onResultado("Não foi possível obter o saldo atual.");
                            }
                            return;
                        }

                        if (successCallback != null) {
                            successCallback.onResultado(response.body().getSaldo());
                        }
                    }

                    @Override
                    public void onFailure(Call<ao.uan.fc.dam.mobile.network.dto.SaldoResponseDto> call, Throwable t) {
                        Log.e(TAG, "Falha de ligação ao consultar saldo", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao obter o saldo.");
                        }
                    }
                });
    }

}
