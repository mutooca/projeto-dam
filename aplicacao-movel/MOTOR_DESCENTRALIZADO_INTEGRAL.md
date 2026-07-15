# MOTOR DESCENTRALIZADO - CÓDIGO FONTE INTEGRAL

Este ficheiro contém a implementação completa de todas as classes responsáveis pela rede P2P, UDP e WiFi Direct.

---

## 📂 1. PROTOCOLO E MODELOS

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
    public Message(String type, String sender, String payload) {
        this.type = type;
        this.sender = sender;
        this.payload = payload;
    }
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

---

## 📂 2. INFRAESTRUTURA DE REDE (UDP)

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

## 📂 3. PROCESSAMENTO E GESTÃO

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
            case Protocol.PROFILE_REQUEST: processarProfileRequest(mensagem, origem); break;
            case Protocol.PROFILE_RESPONSE: processarProfileResponse(mensagem); break;
            case Protocol.ADVERTISEMENT: processarAdvertisement(mensagem, origem); break;
            case Protocol.ACK: Log.d(TAG, "ACK recebido"); break;
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
        Message pedido = new Message();
        pedido.setType(Protocol.PROFILE_REQUEST);
        pedido.setSender(sessionManager.getNome());
        enviar(pedido, origem);
    }

    private void processarProfileRequest(Message mensagem, InetAddress origem) {
        atributoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(), atributos -> {
            Map<String, String> perfil = new HashMap<>();
            for (AtributoPerfil a : atributos) perfil.put(a.getChave(), a.getValor());
            Message resposta = new Message();
            resposta.setType(Protocol.PROFILE_RESPONSE);
            resposta.setSender(sessionManager.getNome());
            resposta.setPayload(JsonConverter.toJson(perfil));
            enviar(resposta, origem);
        });
    }

    private void processarProfileResponse(Message mensagem) {
        Log.d(TAG, "Perfil recebido de " + mensagem.getSender());
    }

    private void processarAdvertisement(Message mensagem, InetAddress origem) {
        LocalDateTime agora = LocalDateTime.now();
        if (mensagem.getDataInicio() != null && agora.isBefore(LocalDateTime.parse(mensagem.getDataInicio()))) return;
        if (mensagem.getDataFim() != null && agora.isAfter(LocalDateTime.parse(mensagem.getDataFim()))) return;

        atributoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(), atributos -> {
            Map<String, String> perfil = new HashMap<>();
            for (AtributoPerfil a : atributos) perfil.put(a.getChave(), a.getValor());
            if (!ProfileMatcher.aceitar(mensagem.getPoliticaTipo(), mensagem.getPoliticaChaves(), perfil)) return;

            localRepository.buscarCompleto(Integer.parseInt(mensagem.getLocal()), target -> {
                if (target == null) return;
                localizacaoProvider.obterLocalizacao((lat, lon) -> {
                    String ssid = localizacaoProvider.obterSsid();
                    if (!LocationMatcher.estaNoLocal(target, lat, lon, ssid)) return;

                    AnuncioRecebido anuncio = new AnuncioRecebido();
                    anuncio.setMsgId(mensagem.getMsgId());
                    anuncio.setAutor(mensagem.getAutor());
                    anuncio.setTitulo(mensagem.getTitulo());
                    anuncio.setConteudo(mensagem.getConteudo());
                    anuncio.setLocal(target.getLocal().getNome());
                    anuncio.setPoliticaTipo(mensagem.getPoliticaTipo());
                    anuncio.setPoliticaChaves(mensagem.getPoliticaChaves());
                    anuncio.setDataRececao(LocalDateTime.now());
                    
                    anuncioRepository.receberAnuncioDescentralizado(anuncio, id -> {
                        if (id > 0) NotificationHelper.mostrarNotificacaoAnuncio(context, anuncio.getMsgId(), anuncio.getTitulo(), anuncio.getConteudo());
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

### **WifiDirectManager.java**
```java
package ao.uan.fc.dam.mobile.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectManager {
    private static final String TAG = "WifiDirectManager";
    private final Context context;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final IntentFilter intentFilter;
    private final WifiDirectListener listener;
    private BroadcastReceiver receiver;
    private boolean registado = false;
    private boolean ligado = false;
    private String enderecoLocal;
    private boolean aLigar = false;

    public interface WifiDirectListener {
        void onPeersDisponiveis(List<WifiP2pDevice> peers);
        void onLigacaoEstabelecida(WifiP2pInfo info);
        void onWifiDirectIndisponivel();
    }

    public WifiDirectManager(Context context, WifiDirectListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void registar() {
        if (registado) return;
        receiver = new BroadcastReceiver() {
            @Override
            @SuppressLint("MissingPermission")
            public void onReceive(Context ctx, Intent intent) {
                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                    manager.requestPeers(channel, peerList -> listener.onPeersDisponiveis(new ArrayList<>(peerList.getDeviceList())));
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                    manager.requestConnectionInfo(channel, info -> {
                        if (info.groupFormed) {
                            ligado = true;
                            aLigar = false;
                            String ip = info.isGroupOwner ? "192.168.49.2" : info.groupOwnerAddress.getHostAddress();
                            try { PeerManager.adicionar(new Peer("WiFiDirect", java.net.InetAddress.getByName(ip), 8888)); } catch (Exception ignored) {}
                            listener.onLigacaoEstabelecida(info);
                        }
                    });
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                    WifiP2pDevice d = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    if (d != null) enderecoLocal = d.deviceAddress;
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED);
        else context.registerReceiver(receiver, intentFilter);
        registado = true;
    }

    @SuppressLint("MissingPermission")
    public void descobrirPeers() { manager.discoverPeers(channel, null); }

    @SuppressLint("MissingPermission")
    public void conectarComDesempate(WifiP2pDevice device) {
        if (aLigar || ligado || enderecoLocal == null) return;
        if (enderecoLocal.compareTo(device.deviceAddress) >= 0) return;
        aLigar = true;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, null);
    }

    public void cancelarRegistro() {
        if (registado) context.unregisterReceiver(receiver);
        registado = false;
    }
}
```

---

## 📂 4. AUXILIARES E CONVERSORES

### **JsonConverter.java**
```java
package ao.uan.fc.dam.mobile.network;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class JsonConverter {
    private static final Gson gson = new Gson();
    public static String toJson(Object o) { return gson.toJson(o); }
    public static <T> T fromJson(String j, Class<T> c) { return gson.fromJson(j, c); }
    public static Map<String, String> toMap(String j) { return gson.fromJson(j, new TypeToken<Map<String, String>>(){}.getType()); }
}
```

### **ProfileMatcher.java**
```java
package ao.uan.fc.dam.mobile.network;
import java.util.Map;

public class ProfileMatcher {
    public static boolean aceitar(String politica, String restricao, Map<String, String> perfil) {
        if (restricao == null || restricao.isEmpty()) return true;
        String[] partes = restricao.split("=");
        if (partes.length != 2) return true;
        String vPerfil = perfil.get(partes[0].trim());
        if (vPerfil == null) return "BLACKLIST".equalsIgnoreCase(politica);
        boolean igual = partes[1].trim().equalsIgnoreCase(vPerfil);
        return "WHITELIST".equalsIgnoreCase(politica) ? igual : !igual;
    }
}
```

### **LocationMatcher.java**
```java
package ao.uan.fc.dam.mobile.network;
import android.location.Location;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;

public class LocationMatcher {
    public static boolean estaNoLocal(LocalCompleto target, double lat, double lon, String ssid) {
        CoordenadaWifi w = target.getCoordenadaWifi();
        if (w != null && w.getSsid() != null && w.getSsid().equalsIgnoreCase(ssid)) return true;
        CoordenadaGps g = target.getCoordenadaGps();
        if (g != null && !Double.isNaN(lat)) {
            float[] res = new float[1];
            Location.distanceBetween(lat, lon, g.getLatitude(), g.getLongitude(), res);
            return res[0] <= g.getRaio();
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
    private final FusedLocationProviderClient fused;
    private final Context context;
    public LocalizacaoProvider(Context c) { this.context = c; fused = LocationServices.getFusedLocationProviderClient(c); }
    @SuppressLint("MissingPermission")
    public void obterLocalizacao(LocalizacaoCallback cb) {
        fused.getLastLocation().addOnSuccessListener(l -> { if(l != null) cb.onResultado(l.getLatitude(), l.getLongitude()); else cb.onResultado(Double.NaN, Double.NaN); });
    }
    public String obterSsid() {
        WifiInfo i = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        String s = i != null ? i.getSSID() : null;
        return (s != null && s.startsWith("\"")) ? s.substring(1, s.length() - 1) : s;
    }
}
```
