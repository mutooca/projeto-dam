package ao.uan.fc.dam.mobile.ui.model;

public class AnuncioModel {

    private int id;
    private String msgId;
    private String titulo;
    private String autor;
    private String local;
    private String conteudo;
    private String modoEntrega;


    public AnuncioModel(
            int id,
            String titulo,
            String autor,
            String local,
            String conteudo,
            String modoEntrega
    ){

        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.local = local;
        this.conteudo = conteudo;
        this.modoEntrega = modoEntrega;

    }

    public AnuncioModel(
            int id,
            String msgId,
            String titulo,
            String autor,
            String local,
            String conteudo,
            String modoEntrega
    ){

        this.id = id;
        this.msgId = msgId;
        this.titulo = titulo;
        this.autor = autor;
        this.local = local;
        this.conteudo = conteudo;
        this.modoEntrega = modoEntrega;

    }


    public int getId(){
        return id;
    }

    public String getMsgId() {
        return msgId;
    }


    public String getTitulo(){
        return titulo;
    }


    public String getAutor(){
        return autor;
    }


    public String getLocal(){
        return local;
    }


    public String getConteudo(){
        return conteudo;
    }


    public String getModoEntrega(){
        return modoEntrega;
    }
}