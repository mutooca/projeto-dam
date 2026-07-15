package ao.uan.fc.dam.mobile.network.dto;

public class AnuncioInfoResponse {

    private String id;
    private String titulo;
    private String conteudo;
    private String autorEmail;
    private String dataPublicacao;
    private String nomeLocal;
    private String estado;

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getAutorEmail() {
        return autorEmail;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public String getEstado() {
        return estado;
    }
}
