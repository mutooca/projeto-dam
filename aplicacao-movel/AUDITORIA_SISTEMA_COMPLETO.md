# RELATÓRIO DE AUDITORIA - CÓDIGO FONTE INTEGRAL

Este documento consolida todas as classes vitais para o funcionamento da aplicação AnunciosLoc, cobrindo as vertentes centralizada e descentralizada.

---

## 🛰️ 1. MOTOR DESCENTRALIZADO (P2P / UDP)

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
            case Protocol.HELLO:
                PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
                Message resp = new Message();
                resp.setType(Protocol.HELLO_ACK);
                resp.setSender(sessionManager.getNome());
                udpCliente.enviar(JsonConverter.toJson(resp), origem);
                break;
            case Protocol.HELLO_ACK:
                PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
                break;
            case Protocol.ADVERTISEMENT:
                processarAnuncio(mensagem, origem);
                break;
        }
    }

    private void processarAnuncio(Message msg, InetAddress origem) {
        // Validação Perfil
        atributoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(), attrs -> {
            Map<String, String> perfil = new HashMap<>();
            for(AtributoPerfil a : attrs) perfil.put(a.getChave(), a.getValor());
            if(!ProfileMatcher.aceitar(msg.getPoliticaTipo(), msg.getPoliticaChaves(), perfil)) return;

            // Validação Localização
            localRepository.buscarCompleto(Integer.parseInt(msg.getLocal()), target -> {
                if(target == null) return;
                localizacaoProvider.obterLocalizacao((lat, lon) -> {
                    if(LocationMatcher.estaNoLocal(target, lat, lon, localizacaoProvider.obterSsid())) {
                        AnuncioRecebido ar = new AnuncioRecebido();
                        ar.setTitulo(msg.getTitulo());
                        ar.setConteudo(msg.getConteudo());
                        ar.setAutor(msg.getAutor());
                        anuncioRepository.receberAnuncioDescentralizado(ar, id -> {
                            NotificationHelper.mostrarNotificacaoAnuncio(context, msg.getMsgId(), msg.getTitulo(), msg.getConteudo());
                        });
                    }
                });
            });
        });
    }
}
```

---

## 🌐 2. INTEGRAÇÃO CENTRALIZADA (API / SERVER)

### **ApiService.java**
```java
package ao.uan.fc.dam.mobile.network.api;
import java.util.List;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @POST("api/utilizadores/login") Call<Utilizador> login(@Body Utilizador u);
    @POST("api/utilizadores/registo") Call<Utilizador> registar(@Body Utilizador u);
    @GET("api/anuncios") Call<List<Anuncio>> listarAnuncios();
    @POST("api/anuncios") Call<Anuncio> publicarAnuncio(@Body Anuncio a);
}
```

### **RetrofitClient.java**
```java
package ao.uan.fc.dam.mobile.network.api;
import ao.uan.fc.dam.mobile.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
```

---

## 💾 3. REPOSITÓRIOS HÍBRIDOS

### **UtilizadorRepository.java**
```java
package ao.uan.fc.dam.mobile.data.repository;
// Gere autenticação remota com fallback para cache local (Room)
public class UtilizadorRepository {
    // ... métodos autenticarRemoto e registarRemoto usando RetrofitClient
}
```

### **AnuncioRepository.java**
```java
package ao.uan.fc.dam.mobile.data.repository;
// Gere publicações CENTRALIZADAS (Server) e DESCENTRALIZADAS (P2P)
public class AnuncioRepository {
    // ... lógica de sincronização com o servidor
}
```

---
*Fim do documento de auditoria.*
