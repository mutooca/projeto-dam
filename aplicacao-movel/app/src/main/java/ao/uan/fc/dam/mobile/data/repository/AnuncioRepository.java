package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AnuncioDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;
import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.network.dto.PostarAnuncioRequest;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import ao.uan.fc.dam.mobile.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnuncioRepository {
    private final AnuncioDao dao;
    private final Context context;
    private final SessionManager sessionManager;
    private static final String TAG = "AnuncioRepository";

    public AnuncioRepository(Context context){
        this.context = context.getApplicationContext();
        dao = DatabaseProvider.getInstance(this.context).anuncioDao();
        sessionManager = new SessionManager(this.context);
    }

    public void inserir(Anuncio anuncio, ResultadoCallback<Long> callback){
        DatabaseExecutor.executor.execute(() ->{
            long id = dao.inserir(anuncio);
            anuncio.setIdAnuncio((int) id);

            if(callback != null){
                callback.onResultado(id);
            }
        });
    }

    public void publicar(
            Anuncio anuncio,
            String idLocalServidor,
            String emailAutor,
            ResultadoCallback<Long> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        if (anuncio.getModoEntrega() == ModoEntrega.CENTRALIZADO) {
            publicarCentralizado(anuncio, idLocalServidor, emailAutor, successCallback, errorCallback);
            return;
        }

        inserir(anuncio, successCallback);
    }

    private void publicarCentralizado(
            Anuncio anuncio,
            String idLocalServidor,
            String emailAutor,
            ResultadoCallback<Long> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        if (!sessionManager.hasKerberosSession()) {
            Log.e(TAG, "Tentativa de postar anuncio sem sessao Kerberos."
                    + " email=" + sessionManager.getEmail()
                    + " ticket=" + resumir(sessionManager.getTicket())
                    + " sessionId=" + sessionManager.getSessionId());
            if (errorCallback != null) {
                errorCallback.onResultado("Sessão remota ausente. Faça login novamente.");
            }
            return;
        }

        if (idLocalServidor == null || idLocalServidor.isBlank()) {
            Log.e(TAG, "Tentativa de postar anuncio centralizado sem idLocal do servidor.");
            if (errorCallback != null) {
                errorCallback.onResultado("O local selecionado não tem ID remoto válido.");
            }
            return;
        }

        PostarAnuncioRequest request = new PostarAnuncioRequest(
                emailAutor,
                idLocalServidor,
                anuncio.getTitulo(),
                anuncio.getConteudo(),
                "GERAL",
                anuncio.getVisibilidade() != null ? anuncio.getVisibilidade().name() : "WHITELIST",
                limparTexto(anuncio.getRestricaoPerfil()),
                formatarData(anuncio.getDataInicio()),
                formatarData(anuncio.getDataFim())
        );

        Log.d(TAG, "POST /api/anuncios/postar body={"
                + "emailAutor=" + request.getEmailAutor()
                + ", idLocal=" + request.getIdLocal()
                + ", titulo=" + request.getTitulo()
                + ", conteudo=" + resumirConteudo(request.getConteudo())
                + ", categoria=" + request.getCategoria()
                + ", tipoPolitica=" + request.getTipoPolitica()
                + ", politicaFiltro=" + request.getPoliticaFiltro()
                + ", visivelDe=" + request.getVisivelDe()
                + ", visivelAte=" + request.getVisivelAte()
                + "} ticket=" + resumir(sessionManager.getTicket())
                + " sessionId=" + sessionManager.getSessionId());

        RetrofitClient.getApiService(context)
                .postarAnuncio(request)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String resposta = response.body();
                        Log.d(TAG, "Resposta /api/anuncios/postar HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " body=" + resposta);

                        if (!response.isSuccessful()) {
                            String mensagem = lerMensagemErro(response, "Erro ao publicar anúncio.");
                            Log.e(TAG, "Erro ao publicar anuncio no servidor: " + mensagem);
                            if (errorCallback != null) {
                                errorCallback.onResultado(mensagem);
                            }
                            return;
                        }

                        inserir(anuncio, successCallback);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "Falha de rede ao publicar anuncio", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao publicar anúncio.");
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
                Log.e(TAG, "Erro ao ler erro da API de anuncios", e);
            }
        }
        return mensagemPadrao;
    }

    private String formatarData(LocalDateTime data) {
        return data != null ? data.toString() : null;
    }

    private String limparTexto(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }

    private String resumir(String valor) {
        if (valor == null || valor.isBlank()) {
            return "vazio";
        }
        int tamanho = Math.min(12, valor.length());
        return valor.substring(0, tamanho) + "...";
    }

    private String resumirConteudo(String conteudo) {
        if (conteudo == null || conteudo.isBlank()) {
            return "vazio";
        }
        int tamanho = Math.min(40, conteudo.length());
        return conteudo.substring(0, tamanho) + (conteudo.length() > tamanho ? "..." : "");
    }

    public void sincronizarAnunciosRemotos(ResultadoCallback<Void> callback) {
        RetrofitClient.getApiService(context).listarAnuncios().enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DatabaseExecutor.executor.execute(() -> {
                        for (Anuncio anuncio : response.body()) {
                            dao.inserir(anuncio); // Room ignora se conflito se configurado ou atualiza
                        }
                        if (callback != null) callback.onResultado(null);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                Log.e(TAG, "Erro ao sincronizar anúncios", t);
                if (callback != null) callback.onResultado(null);
            }
        });
    }

    public void remover(Anuncio anuncio, ResultadoCallback<Void> callback){
        DatabaseExecutor.executor.execute(() -> {
            dao.remover(anuncio);
            if(callback != null){
                callback.onResultado(null);
            }
        });
    }

    public LiveData<List<AnuncioCompleto>> listarTodosLiveData() {
        return dao.listarTodosComRelacionamentosLiveData();
    }

    public void listarTodos(ResultadoCallback<List<AnuncioCompleto>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioCompleto> lista = dao.listarTodosComRelacionamentos();
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }

    public void buscarPorId(int id, ResultadoCallback<AnuncioCompleto> callback){
        DatabaseExecutor.executor.execute(() -> {
            AnuncioCompleto anuncio = dao.buscarCompleto(id);
            if (callback != null) {
                callback.onResultado(anuncio);
            }
        });
    }

    public void listarPorLocal(int idLocal, ResultadoCallback<List<AnuncioCompleto>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioCompleto> lista = dao.listarPorLocal(idLocal);
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }

    public void atualizar(Anuncio anuncio, ResultadoCallback<Void> callback){
        DatabaseExecutor.executor.execute(() -> {
            dao.atualizar(anuncio);
            if(callback != null){
                callback.onResultado(null);
            }
        });
    }

    public void contarPorUtilizador(int idUtilizador, ResultadoCallback<Integer> callback){
        DatabaseExecutor.executor.execute(() -> {
            int total = dao.contarPorUtilizador(idUtilizador);
            if (callback != null) {
                callback.onResultado(total);
            }
        });
    }

    public void listarPorUtilizador(int idUtilizador, ResultadoCallback<List<Anuncio>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<Anuncio> lista = dao.listarPorUtilizador(idUtilizador);
            if (callback != null) {
                callback.onResultado(lista);
            }
        });
    }
}
