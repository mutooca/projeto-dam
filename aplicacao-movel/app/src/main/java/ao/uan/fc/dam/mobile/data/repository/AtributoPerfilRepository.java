package ao.uan.fc.dam.mobile.data.repository;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AtributoPerfilDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.network.dto.MensagemResponse;
import ao.uan.fc.dam.mobile.network.dto.PerfilItemDto;
import ao.uan.fc.dam.mobile.network.dto.PerfilRequestDto;
import ao.uan.fc.dam.mobile.network.dto.PerfilResponseDto;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import ao.uan.fc.dam.mobile.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtributoPerfilRepository {

    private static final String TAG = "AtributoPerfilRepo";

    private final Context context;
    private final AtributoPerfilDao dao;
    private final SessionManager sessionManager;

    public AtributoPerfilRepository(Context context) {
        this.context = context.getApplicationContext();
        dao = DatabaseProvider.getInstance(this.context).atributoPerfilDao();
        sessionManager = new SessionManager(this.context);
    }

    public void inserir(AtributoPerfil atributo, ResultadoCallback<Long> callback) {
        DatabaseExecutor.executor.execute(() -> {
            long id = dao.inserir(atributo);
            if (callback != null) callback.onResultado(id);
        });
    }

    public void remover(AtributoPerfil atributo, ResultadoCallback<Void> callback) {
        DatabaseExecutor.executor.execute(() -> {
            dao.remover(atributo);
            if (callback != null) callback.onResultado(null);
        });
    }

    public void listarPorUtilizador(int idUtilizador, ResultadoCallback<List<AtributoPerfil>> callback) {
        DatabaseExecutor.executor.execute(() -> {
            List<AtributoPerfil> lista = dao.listarPorUtilizador(idUtilizador);
            callback.onResultado(lista);
        });
    }

    public void buscarPorChave(int idUtilizador, String chave, ResultadoCallback<AtributoPerfil> callback) {
        DatabaseExecutor.executor.execute(() -> {
            AtributoPerfil atributo = dao.buscarPorChave(idUtilizador, chave);
            callback.onResultado(atributo);
        });
    }

    public void listarChavesDistintas(ResultadoCallback<List<String>> callback) {
        DatabaseExecutor.executor.execute(() -> {
            List<String> lista = dao.listarChavesDistintas();
            callback.onResultado(lista);
        });
    }

    /**
     * Catálogo global: pares chave=valor criados por QUALQUER utilizador (não só o local).
     * Usado para o utilizador escolher atributos existentes em vez de digitar às cegas.
     */
    public void listarCatalogoRemoto(
            ResultadoCallback<List<PerfilItemDto>> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        Log.d(TAG, "GET /api/perfil/todos");
        RetrofitClient.getApiService(context)
                .listarCatalogoPerfilRemoto()
                .enqueue(new Callback<List<PerfilItemDto>>() {
                    @Override
                    public void onResponse(Call<List<PerfilItemDto>> call, Response<List<PerfilItemDto>> response) {
                        Log.d(TAG, "Resposta GET /api/perfil/todos HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " quantidade=" + (response.body() != null ? response.body().size() : 0));

                        if (response.isSuccessful() && response.body() != null) {
                            if (successCallback != null) {
                                successCallback.onResultado(response.body());
                            }
                            return;
                        }
                        if (errorCallback != null) {
                            errorCallback.onResultado(lerMensagemErro(response, "Erro ao carregar catálogo de atributos."));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PerfilItemDto>> call, Throwable t) {
                        Log.e(TAG, "Falha ao carregar catálogo de atributos", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao carregar catálogo de atributos.");
                        }
                    }
                });
    }

    /**
     * Perfil remoto do próprio utilizador autenticado (fonte de verdade partilhada).
     */
    public void listarMeuPerfilRemoto(
            ResultadoCallback<List<PerfilItemDto>> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        String email = sessionManager.getEmail();
        Log.d(TAG, "GET /api/perfil?email=" + email);
        RetrofitClient.getApiService(context)
                .consultarPerfil(email)
                .enqueue(new Callback<PerfilResponseDto>() {
                    @Override
                    public void onResponse(Call<PerfilResponseDto> call, Response<PerfilResponseDto> response) {
                        Log.d(TAG, "Resposta GET /api/perfil HTTP=" + response.code()
                                + " successful=" + response.isSuccessful());

                        if (response.isSuccessful() && response.body() != null) {
                            List<PerfilItemDto> perfil = response.body().getPerfil();
                            if (successCallback != null) {
                                successCallback.onResultado(perfil != null ? perfil : new ArrayList<>());
                            }
                            return;
                        }
                        if (errorCallback != null) {
                            errorCallback.onResultado(lerMensagemErro(response, "Erro ao carregar o seu perfil."));
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilResponseDto> call, Throwable t) {
                        Log.e(TAG, "Falha ao carregar o meu perfil", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao carregar o seu perfil.");
                        }
                    }
                });
    }

    /**
     * Grava a LISTA COMPLETA de atributos do utilizador (o backend substitui todo o perfil
     * anterior por este, não é incremental). Usar sempre com a lista actual + a alteração desejada.
     */
    public void guardarPerfilRemoto(
            List<PerfilItemDto> perfilCompleto,
            ResultadoCallback<String> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        String email = sessionManager.getEmail();
        Log.d(TAG, "POST /api/perfil email=" + email + " atributos=" + perfilCompleto.size());

        RetrofitClient.getApiService(context)
                .adicionarPerfil(new PerfilRequestDto(email, perfilCompleto))
                .enqueue(new Callback<MensagemResponse>() {
                    @Override
                    public void onResponse(Call<MensagemResponse> call, Response<MensagemResponse> response) {
                        Log.d(TAG, "Resposta POST /api/perfil HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " sucesso=" + (response.body() != null && response.body().isSucesso()));

                        if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                            if (successCallback != null) {
                                successCallback.onResultado(response.body().getMensagem());
                            }
                            return;
                        }
                        if (errorCallback != null) {
                            errorCallback.onResultado(lerMensagemErro(response, "Erro ao guardar atributo de perfil."));
                        }
                    }

                    @Override
                    public void onFailure(Call<MensagemResponse> call, Throwable t) {
                        Log.e(TAG, "Falha ao guardar perfil remotamente", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao guardar atributo de perfil.");
                        }
                    }
                });
    }

    public void removerChaveRemota(
            String chave,
            ResultadoCallback<String> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        String email = sessionManager.getEmail();
        Log.d(TAG, "DELETE /api/perfil/" + chave + "?email=" + email);

        RetrofitClient.getApiService(context)
                .removerChavePerfilRemoto(chave, email)
                .enqueue(new Callback<MensagemResponse>() {
                    @Override
                    public void onResponse(Call<MensagemResponse> call, Response<MensagemResponse> response) {
                        Log.d(TAG, "Resposta DELETE /api/perfil/" + chave + " HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " sucesso=" + (response.body() != null && response.body().isSucesso()));

                        if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                            if (successCallback != null) {
                                successCallback.onResultado(response.body().getMensagem());
                            }
                            return;
                        }
                        if (errorCallback != null) {
                            errorCallback.onResultado(lerMensagemErro(response, "Erro ao remover atributo de perfil."));
                        }
                    }

                    @Override
                    public void onFailure(Call<MensagemResponse> call, Throwable t) {
                        Log.e(TAG, "Falha ao remover atributo remotamente", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao remover atributo de perfil.");
                        }
                    }
                });
    }

    private String lerMensagemErro(Response<?> response, String mensagemPadrao) {
        if (response != null && response.errorBody() != null) {
            try {
                String erro = response.errorBody().string();
                if (erro != null && !erro.isBlank()) {
                    return erro;
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro ao ler corpo de erro da API de perfil", e);
            }
        }
        return mensagemPadrao;
    }
}