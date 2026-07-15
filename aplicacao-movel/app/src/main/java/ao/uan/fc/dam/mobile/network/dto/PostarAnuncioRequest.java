package ao.uan.fc.dam.mobile.network.dto;

public class PostarAnuncioRequest {

    private final String emailAutor;
    private final String idLocal;
    private final String titulo;
    private final String conteudo;
    private final String categoria;
    private final String tipoPolitica;
    private final String politicaFiltro;
    private final String visivelDe;
    private final String visivelAte;

    public PostarAnuncioRequest(
            String emailAutor,
            String idLocal,
            String titulo,
            String conteudo,
            String categoria,
            String tipoPolitica,
            String politicaFiltro,
            String visivelDe,
            String visivelAte
    ) {
        this.emailAutor = emailAutor;
        this.idLocal = idLocal;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.categoria = categoria;
        this.tipoPolitica = tipoPolitica;
        this.politicaFiltro = politicaFiltro;
        this.visivelDe = visivelDe;
        this.visivelAte = visivelAte;
    }

    public String getEmailAutor() {
        return emailAutor;
    }

    public String getIdLocal() {
        return idLocal;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipoPolitica() {
        return tipoPolitica;
    }

    public String getPoliticaFiltro() {
        return politicaFiltro;
    }

    public String getVisivelDe() {
        return visivelDe;
    }

    public String getVisivelAte() {
        return visivelAte;
    }
}
