package ao.uan.fc.dam.mobile.network.dto;

public class PerfilItemDto {

    private String chave;
    private String valor;

    public PerfilItemDto() {
    }

    public PerfilItemDto(String chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
