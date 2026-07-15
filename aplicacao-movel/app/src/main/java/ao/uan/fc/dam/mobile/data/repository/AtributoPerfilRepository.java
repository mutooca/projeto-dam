package ao.uan.fc.dam.mobile.data.repository;
import  ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;
import android.content.Context;

import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AtributoPerfilDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;

public class AtributoPerfilRepository {

    private final AtributoPerfilDao dao;

    public AtributoPerfilRepository(Context context) {
        dao = DatabaseProvider.getInstance(context).atributoPerfilDao();
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
}