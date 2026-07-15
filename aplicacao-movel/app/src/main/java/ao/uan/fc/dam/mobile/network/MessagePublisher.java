package ao.uan.fc.dam.mobile.network;

import android.content.Context;
import android.util.Log;

import java.net.InetAddress;

import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class MessagePublisher {

    private static final String TAG = "MessagePublisher";
    private final SessionManager sessionManager;
    private final UdpCliente udpCliente;

    public MessagePublisher(Context context){
        sessionManager = new SessionManager(context.getApplicationContext());
        udpCliente = new UdpCliente();
    }

    public void enviarHello(InetAddress destino) {
        Message mensagem = new Message();
        mensagem.setType(Protocol.HELLO);
        mensagem.setSender(sessionManager.getNome());
        
        String json = JsonConverter.toJson(mensagem);
        udpCliente.enviar(json, destino);
        Log.d(TAG, "HELLO enviado para " + destino.getHostAddress());
    }

    public void publicar(Anuncio anuncio){
        Message mensagem = new Message();
        mensagem.setType(Protocol.ADVERTISEMENT);
        mensagem.setSender(sessionManager.getNome());
        mensagem.setMsgId("anuncio-" + anuncio.getIdAnuncio());
        mensagem.setTitulo(anuncio.getTitulo());
        mensagem.setConteudo(anuncio.getConteudo());
        mensagem.setAutor(sessionManager.getNome());
        mensagem.setLocal(String.valueOf(anuncio.getIdLocal()));
        mensagem.setPoliticaTipo(anuncio.getVisibilidade().name());
        mensagem.setPoliticaChaves(anuncio.getRestricaoPerfil());

        // Janela temporal
        if (anuncio.getDataInicio() != null) {
            mensagem.setDataInicio(anuncio.getDataInicio().toString());
        }
        if (anuncio.getDataFim() != null) {
            mensagem.setDataFim(anuncio.getDataFim().toString());
        }

        enviarParaPeers(mensagem);
    }

    private void enviarParaPeers(Message mensagem){
        String json = JsonConverter.toJson(mensagem);

        if(PeerManager.listar().isEmpty()){
            Log.w(TAG, "Nenhum peer conectado via handshake");
            return;
        }

        for(Peer peer : PeerManager.listar()){
            udpCliente.enviar(json, peer.getEndereco());
            Log.d(TAG, "ADVERTISEMENT enviado para " + peer.getNome() + " (" + peer.getEndereco().getHostAddress() + ")");
        }
    }
}
