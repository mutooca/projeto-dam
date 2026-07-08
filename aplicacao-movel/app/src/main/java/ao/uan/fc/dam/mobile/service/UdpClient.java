package ao.uan.fc.dam.mobile.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import ao.uan.fc.dam.mobile.model.Packet;

public class UdpClient {
    private final int PORT = 8888;

    public void sendPacket(String ip, Packet packet) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress address = InetAddress.getByName(ip);
                byte[] data = packet.toJson().getBytes(StandardCharsets.UTF_8);
                
                DatagramPacket datagram = new DatagramPacket(data, data.length, address, PORT);
                socket.send(datagram);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Exemplo de fluxo HELLO
    public void sayHello(String ip) {
        Packet hello = new Packet("HELLO", "client_id", new HashMap<>());
        sendPacket(ip, hello);
    }
}
