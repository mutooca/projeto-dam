package ao.uan.fc.dam.mobile.network.dto;

import java.util.List;

public class PerfilResponseDto {

    private String email;
    private List<PerfilItemDto> perfil;
    private String mensagem;

    public String getEmail() {
        return email;
    }

    public List<PerfilItemDto> getPerfil() {
        return perfil;
    }

    public String getMensagem() {
        return mensagem;
    }
}
