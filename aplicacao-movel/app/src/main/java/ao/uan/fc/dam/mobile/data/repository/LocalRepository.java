package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.CoordenadaGpsDao;
import ao.uan.fc.dam.mobile.data.dao.LocalDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.enums.TipoCoordenada;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;
import ao.uan.fc.dam.mobile.network.api.RetrofitClient;
import ao.uan.fc.dam.mobile.network.dto.CriarLocalRequest;
import ao.uan.fc.dam.mobile.network.dto.CriarLocalResponse;
import ao.uan.fc.dam.mobile.network.dto.LocalProximoResponse;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;
import ao.uan.fc.dam.mobile.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalRepository {

    private static final String TAG = "LocalRepository";
    private final Context context;
    private final LocalDao localDao;
    private final CoordenadaGpsDao coordenadaGpsDao;
    private final SessionManager sessionManager;

    public LocalRepository(Context context){
        this.context = context.getApplicationContext();
        localDao = DatabaseProvider
                .getInstance(this.context)
                .localDao();
        coordenadaGpsDao = DatabaseProvider
                .getInstance(this.context)
                .coordenadaGpsDao();
        sessionManager = new SessionManager(this.context);

    }

    public void criarRemoto(
            String nome,
            double latitude,
            double longitude,
            double raio,
            ResultadoCallback<Local> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        if (!sessionManager.hasKerberosSession()) {
            Log.e(TAG, "Tentativa de criar local sem sessao Kerberos."
                    + " email=" + sessionManager.getEmail()
                    + " ticket=" + resumir(sessionManager.getTicket())
                    + " sessionId=" + sessionManager.getSessionId());
            if (errorCallback != null) {
                errorCallback.onResultado(
                        "Sessão remota ausente. Faça login novamente com o servidor ligado."
                );
            }
            return;
        }

        CriarLocalRequest request = new CriarLocalRequest(
                nome,
                latitude,
                longitude,
                raio,
                sessionManager.getEmail(),
                null
        );

        Log.d(TAG, "POST /api/locais/criar"
                + "?lat=" + latitude
                + "&lon=" + longitude
                + " body={nome=" + nome
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", raio=" + raio
                + ", emailUtilizador=" + sessionManager.getEmail()
                + ", ssidWifi=null}"
                + " ticket=" + resumir(sessionManager.getTicket())
                + " sessionId=" + sessionManager.getSessionId());

        RetrofitClient.getApiService(context)
                .criarLocal(latitude, longitude, request)
                .enqueue(new Callback<CriarLocalResponse>() {
                    @Override
                    public void onResponse(
                            Call<CriarLocalResponse> call,
                            Response<CriarLocalResponse> response
                    ) {
                        Log.d(TAG, "Resposta /api/locais/criar HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " body=" + resumirResposta(response.body()));

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSucesso()) {
                            if (response.body().getIdLocal() == null
                                    || response.body().getIdLocal().isBlank()) {
                                Log.e(TAG, "Servidor respondeu sucesso=true mas sem idLocal remoto.");
                                if (errorCallback != null) {
                                    errorCallback.onResultado("Servidor respondeu sem id do local.");
                                }
                                return;
                            }

                            persistirLocalCriado(
                                    nome,
                                    latitude,
                                    longitude,
                                    raio,
                                    response.body().getIdLocal(),
                                    successCallback
                            );
                            return;
                        }

                        String mensagem = extrairMensagemErro(response);
                        Log.e(TAG, "Erro ao criar local no servidor: " + mensagem);
                        if (errorCallback != null) {
                            errorCallback.onResultado(mensagem);
                        }
                    }

                    @Override
                    public void onFailure(Call<CriarLocalResponse> call, Throwable t) {
                        Log.e(TAG, "Falha ao criar local remotamente", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao criar local.");
                        }
                    }
                });
    }

    public void listarProximosRemoto(
            double latitudeUtilizador,
            double longitudeUtilizador,
            ResultadoCallback<List<LocalCompleto>> successCallback,
            ResultadoCallback<String> errorCallback
    ) {
        if (!sessionManager.hasKerberosSession()) {
            Log.e(TAG, "Tentativa de listar locais sem sessao Kerberos."
                    + " email=" + sessionManager.getEmail()
                    + " ticket=" + resumir(sessionManager.getTicket())
                    + " sessionId=" + sessionManager.getSessionId());
            if (errorCallback != null) {
                errorCallback.onResultado(
                        "Sessão remota ausente. Faça login novamente com o servidor ligado."
                );
            }
            return;
        }

        Log.d(TAG, "GET /api/locais/proximos"
                + "?lat=" + latitudeUtilizador
                + "&lon=" + longitudeUtilizador
                + " ticket=" + resumir(sessionManager.getTicket())
                + " sessionId=" + sessionManager.getSessionId());

        RetrofitClient.getApiService(context)
                .listarLocaisProximos(latitudeUtilizador, longitudeUtilizador)
                .enqueue(new Callback<List<LocalProximoResponse>>() {
                    @Override
                    public void onResponse(
                            Call<List<LocalProximoResponse>> call,
                            Response<List<LocalProximoResponse>> response
                    ) {
                        Log.d(TAG, "Resposta /api/locais/proximos HTTP=" + response.code()
                                + " successful=" + response.isSuccessful()
                                + " quantidade=" + (response.body() != null ? response.body().size() : 0));

                        if (!response.isSuccessful() || response.body() == null) {
                            String mensagem = lerMensagemErroGenerica(response);
                            Log.e(TAG, "Erro ao listar locais proximos: " + mensagem);
                            if (errorCallback != null) {
                                errorCallback.onResultado(mensagem);
                            }
                            return;
                        }

                        List<LocalCompleto> locais = mapearLocaisRemotos(response.body());
                        Log.d(TAG, "Locais proximos mapeados para UI: " + locais.size());
                        if (successCallback != null) {
                            successCallback.onResultado(locais);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LocalProximoResponse>> call, Throwable t) {
                        Log.e(TAG, "Falha ao listar locais proximos remotamente", t);
                        if (errorCallback != null) {
                            errorCallback.onResultado("Falha de ligação ao listar locais próximos.");
                        }
                    }
                });
    }

    public void inserir(Local local,
                        ResultadoCallback<Long> callback){

        DatabaseExecutor.executor.execute(() ->{

            long id = localDao.inserir(local);

            callback.onResultado(id);

        });

    }

    private void persistirLocalCriado(
            String nome,
            double latitude,
            double longitude,
            double raio,
            String idServidor,
            ResultadoCallback<Local> callback
    ) {
        DatabaseExecutor.executor.execute(() -> {
            Local local = new Local();
            local.setNome(nome);
            local.setIdServidor(idServidor);
            local.setTipoCoordenada(TipoCoordenada.GPS);

            long idLocal = localDao.inserir(local);
            local.setIdLocal((int) idLocal);
            Log.d(TAG, "Local persistido na Room apos confirmacao remota."
                    + " idLocalLocal=" + idLocal
                    + " idLocalServidor=" + idServidor
                    + " nome=" + nome);

            CoordenadaGps gps = new CoordenadaGps();
            gps.setLatitude(latitude);
            gps.setLongitude(longitude);
            gps.setRaio(raio);
            gps.setIdLocal((int) idLocal);
            coordenadaGpsDao.inserir(gps);
            Log.d(TAG, "Coordenada GPS persistida na Room para idLocalLocal=" + idLocal
                    + " lat=" + latitude + " lon=" + longitude + " raio=" + raio);

            if (callback != null) {
                callback.onResultado(local);
            }
        });
    }

    private String extrairMensagemErro(Response<CriarLocalResponse> response) {
        if (response.body() != null
                && response.body().getMensagem() != null
                && !response.body().getMensagem().isBlank()) {
            return response.body().getMensagem();
        }

        if (response.errorBody() != null) {
            try {
                String erro = response.errorBody().string();
                if (erro != null && !erro.isBlank()) {
                    return erro;
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro ao ler corpo de erro da API", e);
            }
        }

        return "Erro ao criar local.";
    }

    private String lerMensagemErroGenerica(Response<?> response) {
        if (response == null) {
            return "Resposta inválida do servidor.";
        }

        if (response.errorBody() != null) {
            try {
                String erro = response.errorBody().string();
                if (erro != null && !erro.isBlank()) {
                    return erro;
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro ao ler corpo de erro da API", e);
            }
        }

        return "Erro ao carregar locais próximos.";
    }

    private List<LocalCompleto> mapearLocaisRemotos(List<LocalProximoResponse> resposta) {
        List<LocalCompleto> locais = new ArrayList<>();
        for (LocalProximoResponse remoto : resposta) {
            Local local = new Local();
            local.setIdLocal(0);
            local.setIdServidor(remoto.getIdLocal());
            local.setNome(remoto.getNome());
            local.setTipoCoordenada(remoto.getLatitude() != null && remoto.getLongitude() != null
                    ? TipoCoordenada.GPS
                    : TipoCoordenada.WIFI);

            CoordenadaGps gps = null;
            if (remoto.getLatitude() != null && remoto.getLongitude() != null) {
                gps = new CoordenadaGps();
                gps.setLatitude(remoto.getLatitude());
                gps.setLongitude(remoto.getLongitude());
                gps.setRaio(remoto.getRaio() != null ? remoto.getRaio() : 0.0);
                gps.setIdLocal(0);
            }

            CoordenadaWifi wifi = null;
            if (remoto.getSsid() != null && !remoto.getSsid().isBlank()) {
                wifi = new CoordenadaWifi();
                wifi.setSsid(remoto.getSsid());
                wifi.setIdLocal(0);
            }

            LocalCompleto completo = new LocalCompleto();
            completo.setLocal(local);
            completo.setCoordenadaGps(gps);
            completo.setCoordenadaWifi(wifi);
            completo.setDistanciaMetros(remoto.getDistancia());
            locais.add(completo);
        }
        return locais;
    }

    private String resumir(String valor) {
        if (valor == null || valor.isBlank()) {
            return "vazio";
        }
        int tamanho = Math.min(12, valor.length());
        return valor.substring(0, tamanho) + "...";
    }

    private String resumirResposta(CriarLocalResponse resposta) {
        if (resposta == null) {
            return "null";
        }
        return "{sucesso=" + resposta.isSucesso()
                + ", idLocal=" + resposta.getIdLocal()
                + ", mensagem=" + resposta.getMensagem()
                + ", nome=" + resposta.getNome()
                + ", latitude=" + resposta.getLatitude()
                + ", longitude=" + resposta.getLongitude()
                + ", raio=" + resposta.getRaio()
                + "}";
    }

    public void atualizar(Local local,
                          ResultadoCallback<Void> callback){

        DatabaseExecutor.executor.execute(() ->{

            localDao.atualizar(local);

            if(callback != null){
                callback.onResultado(null);
            }

        });

    }

    public void remover(Local local,
                        ResultadoCallback<Void> callback){

        DatabaseExecutor.executor.execute(() ->{

            localDao.remover(local);

            if(callback != null){
                callback.onResultado(null);
            }

        });

    }

    public void listarTodos(ResultadoCallback<List<Local>> callback){

        DatabaseExecutor.executor.execute(() ->{

            List<Local> lista = localDao.listarTodos();

            callback.onResultado(lista);

        });

    }

    public void buscarPorId(int id, ResultadoCallback<Local> callback){

        DatabaseExecutor.executor.execute(() ->{

            Local local = localDao.buscarPorId(id);

            callback.onResultado(local);

        });
    }

    public void buscarCompleto(int id, ResultadoCallback<LocalCompleto> callback){
        DatabaseExecutor.executor.execute(() ->{
            LocalCompleto local = localDao.buscarCompleto(id);
            callback.onResultado(local);
        });
    }

    public void listarTodosComCoordenadas(
            ResultadoCallback<List<LocalCompleto>> callback){


        DatabaseExecutor.executor.execute(() ->{


            List<LocalCompleto> lista =
                    localDao.listarTodosComCoordenadas();


            callback.onResultado(lista);


        });


    }

    public void pesquisar(String nome, ResultadoCallback<List<Local>> callback){
        DatabaseExecutor.executor.execute(() ->{
            List<Local> lista =
                    localDao.pesquisar(nome);
            callback.onResultado(lista);
        });
    }

    public void pesquisarComCoordenadas(
            String nome,
            ResultadoCallback<List<LocalCompleto>> callback){
        DatabaseExecutor.executor.execute(() -> {
            List<LocalCompleto> lista =
                    localDao.pesquisarComCoordenadas(nome);
            callback.onResultado(lista);
        });
    }
}
