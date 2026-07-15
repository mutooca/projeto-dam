package ao.uan.fc.dam.mobile.network.dto;

public class LoginRequest {

    private final String email;
    private final String palavraChave;

    public LoginRequest(String email, String palavraChave) {
        this.email = email;
        this.palavraChave = palavraChave;
    }

    public String getEmail() {
        return email;
    }

    public String getPalavraChave() {
        return palavraChave;
    }
}
