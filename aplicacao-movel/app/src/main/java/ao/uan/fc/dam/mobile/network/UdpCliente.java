package ao.uan.fc.dam.mobile.network;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpCliente {

    public static final int PORT = 8888;

    public void enviar(String mensagem,
                       InetAddress destino){

        new Thread(() -> {

            try{

                DatagramSocket socket =
                        new DatagramSocket();

                byte[] dados = mensagem.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet =
                        new DatagramPacket(
                                dados,
                                dados.length,
                                destino,
                                PORT
                        );

                socket.send(packet);

                socket.close();

            }catch (Exception e){

                Log.e(
                        "UDP",
                        "Erro ao enviar",
                        e
                );

            }

        }).start();

    }

}