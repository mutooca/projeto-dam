package ao.uan.fc.dam.mobile.network;

public class Message {

    private String type;
    private String sender;
    private String payload;

    // Identificação da mensagem
    private String msgId;
    private long timestamp;

    // Dados do anúncio
    private String titulo;
    private String conteudo;
    private String autor;
    private String local;
    private String politicaTipo;
    private String politicaChaves;

    // Janela temporal
    private String dataInicio;
    private String dataFim;

    public Message() {
    }

    public Message(String type, String sender, String payload) {
        this.type = type;
        this.sender = sender;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getPoliticaTipo() {
        return politicaTipo;
    }

    public void setPoliticaTipo(String politicaTipo) {
        this.politicaTipo = politicaTipo;
    }

    public String getPoliticaChaves() {
        return politicaChaves;
    }

    public void setPoliticaChaves(String politicaChaves) {
        this.politicaChaves = politicaChaves;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }
}