package ao.uan.fc.dam.mobile.network.dto;

import java.util.List;

public class PerfilRequestDto {

    private final String email;
    private final List<PerfilItemDto> perfil;

    public PerfilRequestDto(String email, List<PerfilItemDto> perfil) {
        this.email = email;
        this.perfil = perfil;
    }

    public String getEmail() {
        return email;
    }

    public List<PerfilItemDto> getPerfil() {
        return perfil;
    }
}
