# NÚCLEO DESCENTRALIZADO COMPLETO (P2P / UDP / WIFI DIRECT)

Este documento contém o código fonte integral de todas as classes responsáveis pela comunicação descentralizada.

---

## 📄 1. PROTOCOLO E MENSAGENS

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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
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

---

## 📄 2. INFRAESTRUTURA DE REDE (UDP)

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
                Log.d("UDP", "Servidor iniciado na porta " + PORT);
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

## 📄 3. PROCESSAMENTO E PUBLICAÇÃO

### **MessageProcessor.java**
```java
package ao.uan.fc.dam.mobile.network;

import android.content.Context;
import android.util.Log;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;
import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;
import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.util.NotificationHelper;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class MessageProcessor {
    private static final String TAG = "PROTOCOLO";
    private final Context context;
    private final SessionManager sessionManager;
    private final UdpCliente udpCliente;
    private final AtributoPerfilRepository atributoRepository;
    private final AnuncioRecebidoRepository anuncioRepository;
    private final LocalRepository localRepository;
    private final LocalizacaoProvider localizacaoProvider;

    public MessageProcessor(Context context) {
        this.context = context.getApplicationContext();
        this.sessionManager = new SessionManager(this.context);
        this.udpCliente = new UdpCliente();
        this.atributoRepository = new AtributoPerfilRepository(this.context);
        this.anuncioRepository = new AnuncioRecebidoRepository(this.context);
        this.localRepository = new LocalRepository(this.context);
        this.localizacaoProvider = new LocalizacaoProvider(this.context);
    }

    public void processar(String json, InetAddress origem) {
        Message mensagem = JsonConverter.fromJson(json, Message.class);
        if (mensagem == null || mensagem.getType() == null) return;

        switch (mensagem.getType()) {
            case Protocol.HELLO: processarHello(mensagem, origem); break;
            case Protocol.HELLO_ACK: processarHelloAck(mensagem, origem); break;
            case Protocol.ADVERTISEMENT: processarAdvertisement(mensagem, origem); break;
            case Protocol.ACK: Log.d(TAG, "Confirmação recebida"); break;
        }
    }

    private void processarHello(Message mensagem, InetAddress origem) {
        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
        Message resposta = new Message();
        resposta.setType(Protocol.HELLO_ACK);
        resposta.setSender(sessionManager.getNome());
        enviar(resposta, origem);
    }

    private void processarHelloAck(Message mensagem, InetAddress origem) {
        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
    }

    private void processarAdvertisement(Message mensagem, InetAddress origem) {
        LocalDateTime agora = LocalDateTime.now();
        // Validação Temporal
        if (mensagem.getDataInicio() != null && agora.isBefore(LocalDateTime.parse(mensagem.getDataInicio()))) return;
        if (mensagem.getDataFim() != null && agora.isAfter(LocalDateTime.parse(mensagem.getDataFim()))) return;

        // Validação Perfil e Localização
        atributoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(), atributos -> {
            Map<String, String> perfil = new HashMap<>();
            for(AtributoPerfil a : atributos) perfil.put(a.getChave(), a.getValor());

            if (!ProfileMatcher.aceitar(mensagem.getPoliticaTipo(), mensagem.getPoliticaChaves(), perfil)) return;

            localRepository.buscarCompleto(Integer.parseInt(mensagem.getLocal()), localTarget -> {
                if (localTarget == null) return;
                localizacaoProvider.obterLocalizacao((lat, lon) -> {
                    String ssid = localizacaoProvider.obterSsid();
                    if (!LocationMatcher.estaNoLocal(localTarget, lat, lon, ssid)) return;

                    AnuncioRecebido anuncio = new AnuncioRecebido();
                    anuncio.setTitulo(mensagem.getTitulo());
                    anuncio.setConteudo(mensagem.getConteudo());
                    anuncio.setAutor(mensagem.getAutor());
                    anuncioRepository.receberAnuncioDescentralizado(anuncio, id -> {
                        NotificationHelper.mostrarNotificacaoAnuncio(context, mensagem.getMsgId(), mensagem.getTitulo(), mensagem.getConteudo());
                    });
                });
            });
        });
    }

    private void enviar(Message msg, InetAddress destino) {
        udpCliente.enviar(JsonConverter.toJson(msg), destino);
    }
}
```

### **MessagePublisher.java**
```java
package ao.uan.fc.dam.mobile.network;

import android.content.Context;
import android.util.Log;
import java.net.InetAddress;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class MessagePublisher {
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
        udpCliente.enviar(JsonConverter.toJson(mensagem), destino);
    }

    public void publicar(Anuncio anuncio){
        Message mensagem = new Message();
        mensagem.setType(Protocol.ADVERTISEMENT);
        mensagem.setSender(sessionManager.getNome());
        mensagem.setTitulo(anuncio.getTitulo());
        mensagem.setConteudo(anuncio.getConteudo());
        mensagem.setLocal(String.valueOf(anuncio.getIdLocal()));
        mensagem.setPoliticaTipo(anuncio.getVisibilidade().name());
        mensagem.setPoliticaChaves(anuncio.getRestricaoPerfil());
        
        for(Peer peer : PeerManager.listar()){
            udpCliente.enviar(JsonConverter.toJson(mensagem), peer.getEndereco());
        }
    }
}
```

---

## 📄 4. MATCHERS E PROVIDERS

### **LocationMatcher.java**
```java
package ao.uan.fc.dam.mobile.network;

import android.location.Location;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;

public class LocationMatcher {
    public static boolean estaNoLocal(LocalCompleto target, double lat, double lon, String ssid) {
        CoordenadaWifi wifi = target.getCoordenadaWifi();
        if (wifi != null && wifi.getSsid() != null && wifi.getSsid().equalsIgnoreCase(ssid)) return true;

        CoordenadaGps gps = target.getCoordenadaGps();
        if (gps != null) {
            float[] res = new float[1];
            Location.distanceBetween(lat, lon, gps.getLatitude(), gps.getLongitude(), res);
            return res[0] <= gps.getRaio();
        }
        return false;
    }
}
```

### **LocalizacaoProvider.java**
```java
package ao.uan.fc.dam.mobile.contentProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import ao.uan.fc.dam.mobile.util.LocalizacaoCallback;

public class LocalizacaoProvider {
    private final FusedLocationProviderClient fusedLocation;
    private final Context context;

    public LocalizacaoProvider(Context context){
        this.context = context.getApplicationContext();
        fusedLocation = LocationServices.getFusedLocationProviderClient(this.context);
    }

    @SuppressLint("MissingPermission")
    public void obterLocalizacao(LocalizacaoCallback callback){
        fusedLocation.getLastLocation().addOnSuccessListener(location -> {
            if(location != null) callback.onResultado(location.getLatitude(), location.getLongitude());
            else callback.onResultado(Double.NaN, Double.NaN);
        });
    }

    public String obterSsid() {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        if (info != null) {
            String ssid = info.getSSID();
            return (ssid != null && ssid.startsWith("\"")) ? ssid.substring(1, ssid.length() - 1) : ssid;
        }
        return null;
    }
}
```
