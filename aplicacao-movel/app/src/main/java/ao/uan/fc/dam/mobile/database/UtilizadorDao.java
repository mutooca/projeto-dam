package ao.uan.fc.dam.mobile.database;

import ao.uan.fc.dam.mobile.model.Utilizador;

/* JADX INFO: loaded from: classes8.dex */
public interface UtilizadorDao {
    void limparTudo();

    Utilizador obterPorEmail(String email);

    void salvar(Utilizador utilizador);
}