package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;

import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.HistoricoDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Historico;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;

public class HistoricoRepository {

    private final HistoricoDao historicoDao;

    public HistoricoRepository(Context context){
        historicoDao = DatabaseProvider
                .getInstance(context)
                .historicoDao();
    }

    public void inserir(
            Historico historico,
            ResultadoCallback<Long> callback
    ){

        DatabaseExecutor.executor.execute(() ->{

            long id = historicoDao.inserir(historico);

            if(callback != null){
                callback.onResultado(id);
            }

        });

    }

    public void atualizar(
            Historico historico,
            ResultadoCallback<Void> callback
    ){

        DatabaseExecutor.executor.execute(() ->{

            historicoDao.atualizar(historico);

            if(callback != null){
                callback.onResultado(null);
            }

        });

    }

    public void remover(
            Historico historico,
            ResultadoCallback<Void> callback
    ){

        DatabaseExecutor.executor.execute(() ->{

            historicoDao.remover(historico);

            if(callback != null){
                callback.onResultado(null);
            }

        });

    }

    public void listarTodos(
            ResultadoCallback<List<Historico>> callback
    ){

        DatabaseExecutor.executor.execute(() ->{

            List<Historico> lista =
                    historicoDao.listarTodos();

            callback.onResultado(lista);

        });

    }

    public void listarPorUtilizador(
            int idUtilizador,
            ResultadoCallback<List<Historico>> callback
    ){

        DatabaseExecutor.executor.execute(() ->{

            List<Historico> lista =
                    historicoDao.listarPorUtilizador(idUtilizador);

            callback.onResultado(lista);

        });

    }

}