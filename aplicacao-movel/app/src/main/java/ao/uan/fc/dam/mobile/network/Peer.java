package ao.uan.fc.dam.mobile.network;

import java.net.InetAddress;

public class Peer {

    private String nome;

    private InetAddress endereco;

    private int porta;

    public Peer(String nome, InetAddress endereco, int porta){
        this.nome = nome;
        this.endereco = endereco;
        this.porta = porta;
    }

    public String getNome() {
        return nome;
    }

    public InetAddress getEndereco() {
        return endereco;
    }

    public int getPorta() {
        return porta;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;

        if(!(obj instanceof Peer))
            return false;

        Peer outro = (Peer) obj;
        return endereco.equals(outro.endereco);
    }

    @Override
    public int hashCode(){
        return endereco.hashCode();
    }
}