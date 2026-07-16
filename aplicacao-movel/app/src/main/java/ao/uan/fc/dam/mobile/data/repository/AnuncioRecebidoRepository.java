package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AnuncioRecebidoDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.network.dto.AnuncioInfoResponse;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import ao.uan.fc.dam.mobile.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnuncioRecebidoRepository {

    private static final String TAG = "AnuncioRecebidoRepo";

    private final Context context;
    private final AnuncioRecebidoDao dao;
    private final SessionManager sessionManager;

    public AnuncioRecebidoRepository(Context context) {
        this.context = context.getApplicationContext();
        dao = DatabaseProvider
                .getInstance(this.context)
                .anuncioRecebidoDao();
        sessionManager = new SessionManager(this.context);
    }

    public void inserir(AnuncioRecebido anuncio,
                        ResultadoCallback<Long> callback) {
        DatabaseExecutor.executor.execute(() -> {
            long id = dao.inserir(anuncio);
            if (callback != null)
                callback.onResultado(id);
        });
    }

    public void receberAnuncioDescentralizado(AnuncioRecebido anuncio,
                                            ResultadoCallback<Long> callback) {
        DatabaseExecutor.executor.execute(() -> {
            AnuncioRecebido existente = dao.buscarPorMsgId(anuncio.getMsgId());
            if (existente == null) {
                long id = dao.inserir(anuncio);
                if (callback != null) callback.onResultado(id);
            } else {
                if (callback != null) callback.onResultado(-1L);
            }
        });
    }

    public LiveData<List<AnuncioRecebido>> listarTodosLiveData(int idUtilizador) {
        return dao.listarTodosLiveData(idUtilizador);
    }

    public void listarTodos(int idUtilizador, ResultadoCallback<List<AnuncioRecebido>> callback) {
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioRecebido> lista = dao.listarTodos(idUtilizador);
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }

    public void buscarPorMsgId(
            String msgId,
            ResultadoCallback<AnuncioRecebido> callback) {
        DatabaseExecutor.executor.execute(() -> {
            AnuncioRecebido anuncio = dao.buscarPorMsgId(msgId);
            if (callback != null) {
                callback.onResultado(anuncio);
            }
        });
    }

    /**
     * Pede ao servidor os anúncios centralizados elegíveis para este utilizador na localização
     * indicada (o backend já aplica a política whitelist/blacklist com base no perfil real).
     * Os resultados são guardados como "recebidos" (idempotente por msgId).
     */
    public void sincronizarPorLocalizacao(
            double lat,
            double lon,
            ResultadoCallback<Integer> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        if (!sessionManager.hasKerberosSession()) {
            Log.w(TAG, "Sem sessao Kerberos, a ignorar sincronizacao de anuncios por localizacao.");
            return;
        }

        String email = sessionManager.getEmail();
        Log.d(TAG, "GET /api/anuncios/receber-por-localizacao?email=" + email + "&lat=" + lat + "&lon=" + lon);

        RetrofitClient.getApiService(context)
                .receberPorLocalizacao(email, lat, lon)
                .enqueue(new Callback<List<AnuncioInfoResponse>>() {
                    @Override
                    public void onResponse(Call<List<AnuncioInfoResponse>> call, Response<List<AnuncioInfoResponse>> response) {
                        Log.d(TAG, "Resposta receber-por-localizacao HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " quantidade=" + (response.body() != null ? response.body().size() : 0));

                        if (!response.isSuccessful() || response.body() == null) {
                            if (errorCallback != null) {
                                errorCallback.onResultado(lerMensagemErro(response, "Erro ao verificar anúncios próximos."));
                            }
                            return;
                        }

                        int idUtilizador = sessionManager.getIdUtilizador();
                        List<AnuncioInfoResponse> recebidos = response.body();

                        DatabaseExecutor.executor.execute(() -> {
                            int novos = 0;
                            for (AnuncioInfoResponse item : recebidos) {
                                if (item.getId() == null || item.getId().isBlank()) {
                                    continue;
                                }
                                if (dao.buscarPorMsgId(item.getId(), idUtilizador) != null) {
                                    continue;
                                }

                                AnuncioRecebido anuncio = new AnuncioRecebido();
                                anuncio.setMsgId(item.getId());
                                anuncio.setIdUtilizador(idUtilizador);
                                anuncio.setModoEntrega("CENTRALIZADO");
                                anuncio.setAutor(item.getAutorEmail());
                                anuncio.setTitulo(item.getTitulo());
                                anuncio.setConteudo(item.getConteudo());
                                anuncio.setLocal(item.getNomeLocal());
                                anuncio.setPoliticaTipo(item.getTipoPolitica());
                                anuncio.setPoliticaChaves(item.getPoliticaFiltro());
                                anuncio.setDataRececao(LocalDateTime.now());
                                anuncio.setDataInicio(parseDataOpcional(item.getDataPublicacao()));

                                dao.inserir(anuncio);
                                novos++;
                            }

                            Log.d(TAG, novos + " novo(s) anúncio(s) centralizado(s) recebido(s) de " + recebidos.size() + " elegíveis.");
                            if (successCallback != null) {
                                successCallback.onResultado(novos);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<AnuncioInfoResponse>> call, Throwable t) {
                        Log.e(TAG, "Falha ao sincronizar anúncios por localização", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao verificar anúncios próximos.");
                        }
                    }
                });
    }

    private LocalDateTime parseDataOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String lerMensagemErro(Response<?> response, String mensagemPadrao) {
        if (response != null && response.errorBody() != null) {
            try {
                String erro = response.errorBody().string();
                if (erro != null && !erro.isBlank()) {
                    return erro;
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro ao ler corpo de erro da API de anuncios recebidos", e);
            }
        }
        return mensagemPadrao;
    }
}
