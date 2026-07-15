package ao.uan.fc.dam.mobile.network;

import java.util.ArrayList;
import java.util.List;

public class PeerManager {

    private static final List<Peer> peers = new ArrayList<>();

    public static synchronized void adicionar(Peer peer){
        for (int i = 0; i < peers.size(); i++) {
            if (peers.get(i).getEndereco().equals(peer.getEndereco())) {
                peers.set(i, peer); // Atualiza o peer (ex: nome real após handshake)
                return;
            }
        }
        peers.add(peer);
    }

    public static synchronized List<Peer> listar(){
        return new ArrayList<>(peers);
    }

    public static synchronized void limpar(){
        peers.clear();
    }
}
