package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;

import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.LocalDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;

public class LocalRepository {

    private final LocalDao localDao;

    public LocalRepository(Context context){

        localDao = DatabaseProvider
                .getInstance(context)
                .localDao();

    }

    public void inserir(Local local,
                        ResultadoCallback<Long> callback){

        DatabaseExecutor.executor.execute(() ->{

            long id = localDao.inserir(local);

            callback.onResultado(id);

        });

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