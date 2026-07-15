package ao.uan.fc.dam.mobile.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import ao.uan.fc.dam.mobile.data.dao.AnuncioRecebidoDao;
import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.util.DatabaseExecutor;
import ao.uan.fc.dam.mobile.util.ResultadoCallback;

public class AnuncioRecebidoRepository {

    private final AnuncioRecebidoDao dao;

    public AnuncioRecebidoRepository(Context context) {
        dao = DatabaseProvider
                .getInstance(context)
                .anuncioRecebidoDao();
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

    public LiveData<List<AnuncioRecebido>> listarTodosLiveData() {
        return dao.listarTodosLiveData();
    }

    public void listarTodos(ResultadoCallback<List<AnuncioRecebido>> callback) {
        DatabaseExecutor.executor.execute(() -> {
            List<AnuncioRecebido> lista = dao.listarTodos();
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
}
