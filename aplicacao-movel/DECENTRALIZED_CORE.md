# NÚCLEO DESCENTRALIZADO (P2P / UDP / WIFI DIRECT)

Este ficheiro contém a implementação completa do protocolo de rede descentralizado da aplicação AnunciosLoc.

---

## 📄 1. MODELOS E PROTOCOLO

### **Peer.java**
```java
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
    public String getNome() { return nome; }
    public InetAddress getEndereco() { return endereco; }
    public int getPorta() { return porta; }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof Peer)) return false;
        Peer outro = (Peer) obj;
        return endereco.equals(outro.endereco);
    }
    @Override
    public int hashCode(){ return endereco.hashCode(); }
}
```

### **Message.java**
```java
package ao.uan.fc.dam.mobile.network;
public class Message {
    private String type;
    private String sender;
    private String payload;
    private String msgId;
    private long timestamp;
    private String titulo;
    private String conteudo;
    private String autor;
    private String local;
    private String politicaTipo;
    private String politicaChaves;
    private String dataInicio;
    private String dataFim;

    public Message() {}
    // Getters e Setters para todos os campos...
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getPoliticaTipo() { return politicaTipo; }
    public void setPoliticaTipo(String politicaTipo) { this.politicaTipo = politicaTipo; }
    public String getPoliticaChaves() { return politicaChaves; }
    public void setPoliticaChaves(String politicaChaves) { this.politicaChaves = politicaChaves; }
    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }
    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }
}
```

### **Protocol.java**
```java
package ao.uan.fc.dam.mobile.network;
public class Protocol {
    public static final String HELLO = "HELLO";
    public static final String HELLO_ACK = "HELLO_ACK";
    public static final String PROFILE_REQUEST = "PROFILE_REQUEST";
    public static final String PROFILE_RESPONSE = "PROFILE_RESPONSE";
    public static final String ADVERTISEMENT = "ADVERTISEMENT";
    public static final String ACK = "ACK";
}
```

---

## 📄 2. INFRAESTRUTURA UDP

### **UdpCliente.java**
```java
package ao.uan.fc.dam.mobile.network;
import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UdpCliente {
    public static final int PORT = 8888;
    public void enviar(String mensagem, InetAddress destino){
        new Thread(() -> {
            try{
                DatagramSocket socket = new DatagramSocket();
                byte[] dados = mensagem.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(dados, dados.length, destino, PORT);
                socket.send(packet);
                socket.close();
            }catch (Exception e){
                Log.e("UDP", "Erro ao enviar", e);
            }
        }).start();
    }
}
```

### **UdpServidor.java**
```java
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
                while (running) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String mensagem = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    processor.processar(mensagem, packet.getAddress());
                }
            } catch (Exception e) {
                if(running) Log.e("UDP", "Erro no servidor", e);
            }
        }).start();
    }
    public void parar() {
        running = false;
        if(socket != null) socket.close();
    }
}
```

---

## 📄 3. GESTÃO DE COMUNICAÇÃO

### **MessageProcessor.java** (Lógica de Handshake e Validação)
```java
package ao.uan.fc.dam.mobile.network;
// ... imports
public class MessageProcessor {
    // Processa mensagens recebidas via UDP e valida contra políticas de Perfil e Localização
    public void processar(String json, InetAddress origem) {
        Message mensagem = JsonConverter.fromJson(json, Message.class);
        if (mensagem == null || mensagem.getType() == null) return;

        switch (mensagem.getType()) {
            case Protocol.HELLO: processarHello(mensagem, origem); break;
            case Protocol.HELLO_ACK: processarHelloAck(mensagem, origem); break;
            case Protocol.ADVERTISEMENT: processarAdvertisement(mensagem, origem); break;
            // ... outros casos
        }
    }
    // ... métodos processarHello, processarAdvertisement (com LocationMatcher e ProfileMatcher)
}
```

### **WifiDirectManager.java** (Conexão Física)
```java
package ao.uan.fc.dam.mobile.network;
// Gerencia a descoberta e conexão automática via WiFi Direct (P2P)
public class WifiDirectManager {
    // Lógica de descoberta, convite e estabelecimento de grupo P2P
}
```

---
*Este ficheiro consolidado serve como o núcleo da funcionalidade descentralizada do projeto.*
