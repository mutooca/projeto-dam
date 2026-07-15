package ao.uan.fc.dam.mobile.service;

import android.content.Context;

import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;


public class perfilService {
    private final AtributoPerfilRepository repository;

    public perfilService(Context context){
        repository = new AtributoPerfilRepository(context);

    }

    public void obterPerfil(int idUtilizador, Callback callback){
        repository.listarPorUtilizador(idUtilizador, atributos -> {
                    StringBuilder perfil = new StringBuilder();

                    for(AtributoPerfil atributo : atributos){
                        perfil.append(atributo.getChave()).append("=").append(atributo.getValor()).append(";");
                    }
                    callback.resposta(perfil.toString());
                }
        );
    }

    public interface Callback{
        void resposta(String perfil);
    }
}
