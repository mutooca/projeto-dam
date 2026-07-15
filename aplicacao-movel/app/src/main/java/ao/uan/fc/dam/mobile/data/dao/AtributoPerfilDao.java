package ao.uan.fc.dam.mobile.data.dao;
import androidx.room.*;
import java.util.List;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;

@Dao
public interface AtributoPerfilDao {

    @Insert
    long inserir(AtributoPerfil atributo);

    @Update
    void atualizar(AtributoPerfil atributo);

    @Delete
    void remover(AtributoPerfil atributo);

    @Query("SELECT * FROM atributos_perfil WHERE id_utilizador = :idUtilizador")
    List<AtributoPerfil> listarPorUtilizador(int idUtilizador);

    @Query("SELECT * FROM atributos_perfil WHERE id_utilizador = :idUtilizador AND chave = :chave LIMIT 1")
    AtributoPerfil buscarPorChave(int idUtilizador, String chave);

    // As chaves (não os valores) são públicas — útil para o publicador
    // escolher em que atributo restringir um anúncio (2.1.2)
    @Query("SELECT DISTINCT chave FROM atributos_perfil ORDER BY chave")
    List<String> listarChavesDistintas();

    @Query("SELECT * FROM atributos_perfil WHERE id_utilizador = :idUtilizador AND chave = :chave LIMIT 1")
    AtributoPerfil buscarPorChaveSync(int idUtilizador, String chave);
}