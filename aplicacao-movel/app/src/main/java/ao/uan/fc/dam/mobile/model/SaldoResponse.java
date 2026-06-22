package ao.uan.fc.dam.mobile.model;

import java.util.List;

public class SaldoResponse {
    private String email;
    private String nome;
    private Integer saldoGlobal;
    private String preferencias;
    private Long totalAnuncios;
    private Long totalEntregas;
    private List<Object> saldosPorInfra;

    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public Integer getSaldoGlobal() { return saldoGlobal; }
    public String getPreferencias() { return preferencias; }
    public Long getTotalAnuncios() { return totalAnuncios; }
    public Long getTotalEntregas() { return totalEntregas; }
    public List<Object> getSaldosPorInfra() { return saldosPorInfra; }
}
