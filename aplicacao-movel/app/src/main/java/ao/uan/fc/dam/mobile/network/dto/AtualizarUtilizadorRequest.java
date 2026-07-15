package ao.uan.fc.dam.mobile.network.dto;

public class AtualizarUtilizadorRequest {

    private final String email;
    private final String nome;
    private final String novaPalavraChave;
    private final String palavraChaveActual;
    private final String preferenciaAnuncio;

    public AtualizarUtilizadorRequest(
            String email,
            String nome,
            String novaPalavraChave,
            String palavraChaveActual,
            String preferenciaAnuncio
    ) {
        this.email = email;
        this.nome = nome;
        this.novaPalavraChave = novaPalavraChave;
        this.palavraChaveActual = palavraChaveActual;
        this.preferenciaAnuncio = preferenciaAnuncio;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getNovaPalavraChave() {
        return novaPalavraChave;
    }

    public String getPalavraChaveActual() {
        return palavraChaveActual;
    }

    public String getPreferenciaAnuncio() {
        return preferenciaAnuncio;
    }
}
