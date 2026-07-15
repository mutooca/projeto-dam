package ao.uan.fc.dam.mobile.network;

import android.content.Context;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpServidor {
    private final MessageProcessor processor;
    public static final int PORT = 8888;
    private boolean running;
    private DatagramSocket socket;

    public UdpServidor(Context context) {
        processor = new MessageProcessor(context);
    }

    public void iniciar() {
        running = true;

        new Thread(() -> {
            try {
                socket = new DatagramSocket(PORT);
                Log.d("UDP", "Servidor iniciado na porta " + PORT);

                while (running) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String mensagem = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    Log.d("UDP", "Mensagem recebida: " + mensagem);

                    processor.processar(mensagem, packet.getAddress());
                }
            } catch (Exception e) {

                if(running){
                    Log.e("UDP", "Erro no servidor", e);
                } else {
                    Log.d("UDP", "Servidor UDP encerrado");
                }

            }
        }).start();
    }

    public void parar() {
        Log.d("UDP", "PEDIDO PARA PARAR SERVIDOR");

        running = false;

        if(socket != null){
            socket.close();
        }
    }
}