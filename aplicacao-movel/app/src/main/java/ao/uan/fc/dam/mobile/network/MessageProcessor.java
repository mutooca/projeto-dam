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

        if (mensagem == null || mensagem.getType() == null) {
            Log.w(TAG, "Mensagem inválida");
            return;
        }

        switch (mensagem.getType()) {
            case Protocol.HELLO:
                processarHello(mensagem, origem);
                break;
            case Protocol.HELLO_ACK:
                processarHelloAck(mensagem, origem);
                break;
            case Protocol.PROFILE_REQUEST:
                processarProfileRequest(mensagem, origem);
                break;
            case Protocol.PROFILE_RESPONSE:
                processarProfileResponse(mensagem);
                break;
            case Protocol.ADVERTISEMENT:
                processarAdvertisement(mensagem, origem);
                break;
            case Protocol.ACK:
                processarAck(mensagem);
                break;
            default:
                Log.w(TAG, "Tipo desconhecido: " + mensagem.getType());
        }
    }

    private void processarHello(Message mensagem, InetAddress origem) {
        Log.d(TAG, "HELLO recebido de " + mensagem.getSender());
        // Adiciona o peer que enviou o HELLO
        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
        
        Message resposta = new Message();
        resposta.setType(Protocol.HELLO_ACK);
        resposta.setSender(sessionManager.getNome());
        enviar(resposta, origem);
    }

    private void processarHelloAck(Message mensagem, InetAddress origem) {
        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));
        Log.d(TAG, "HELLO_ACK recebido. Peer confirmado: " + mensagem.getSender());
        
        // Opcional: Solicitar perfil após handshake
        Message pedido = new Message();
        pedido.setType(Protocol.PROFILE_REQUEST);
        pedido.setSender(sessionManager.getNome());
        enviar(pedido, origem);
    }

    private void processarProfileRequest(Message mensagem, InetAddress origem) {
        int idUtilizador = sessionManager.getIdUtilizador();
        atributoRepository.listarPorUtilizador(idUtilizador, atributos -> {
            Map<String, String> perfil = new HashMap<>();
            for (AtributoPerfil atributo : atributos) {
                perfil.put(atributo.getChave(), atributo.getValor());
            }
            Message resposta = new Message();
            resposta.setType(Protocol.PROFILE_RESPONSE);
            resposta.setSender(sessionManager.getNome());
            resposta.setPayload(JsonConverter.toJson(perfil));
            enviar(resposta, origem);
        });
    }

    private void processarProfileResponse(Message mensagem) {
        Map<String, String> perfil = JsonConverter.toMap(mensagem.getPayload());
        Log.d(TAG, "Perfil recebido: " + perfil);
    }

    private void processarAdvertisement(Message mensagem, InetAddress origem) {
        LocalDateTime agora = LocalDateTime.now();
        if (mensagem.getDataInicio() != null && !mensagem.getDataInicio().isEmpty()) {
            LocalDateTime inicio = LocalDateTime.parse(mensagem.getDataInicio());
            if (agora.isBefore(inicio)) return;
        }
        if (mensagem.getDataFim() != null && !mensagem.getDataFim().isEmpty()) {
            LocalDateTime fim = LocalDateTime.parse(mensagem.getDataFim());
            if (agora.isAfter(fim)) return;
        }

        int idUtilizador = sessionManager.getIdUtilizador();
        atributoRepository.listarPorUtilizador(idUtilizador, atributos -> {
            Map<String, String> perfil = new HashMap<>();
            for (AtributoPerfil atributo : atributos) {
                perfil.put(atributo.getChave(), atributo.getValor());
            }

            if (!ProfileMatcher.aceitar(mensagem.getPoliticaTipo(), mensagem.getPoliticaChaves(), perfil)) {
                return;
            }

            try {
                int idLocal = Integer.parseInt(mensagem.getLocal());
                localRepository.buscarCompleto(idLocal, localTarget -> {
                    if (localTarget == null) return;

                    localizacaoProvider.obterLocalizacao((lat, lon) -> {
                        String ssidActual = localizacaoProvider.obterSsid();
                        if (!LocationMatcher.estaNoLocal(localTarget, lat, lon, ssidActual)) {
                            return;
                        }

                        AnuncioRecebido anuncio = new AnuncioRecebido();
                        anuncio.setMsgId(mensagem.getMsgId());
                        anuncio.setAutor(mensagem.getAutor());
                        anuncio.setTitulo(mensagem.getTitulo());
                        anuncio.setConteudo(mensagem.getConteudo());
                        anuncio.setLocal(localTarget.getLocal().getNome());
                        anuncio.setPoliticaTipo(mensagem.getPoliticaTipo());
                        anuncio.setPoliticaChaves(mensagem.getPoliticaChaves());
                        anuncio.setDataRececao(LocalDateTime.now());
                        
                        if (mensagem.getDataInicio() != null) anuncio.setDataInicio(LocalDateTime.parse(mensagem.getDataInicio()));
                        if (mensagem.getDataFim() != null) anuncio.setDataFim(LocalDateTime.parse(mensagem.getDataFim()));

                        anuncioRepository.receberAnuncioDescentralizado(anuncio, id -> {
                            if (id > 0) {
                                NotificationHelper.mostrarNotificacaoAnuncio(context, anuncio.getMsgId(), anuncio.getTitulo(), anuncio.getConteudo());
                                Message ack = new Message();
                                ack.setType(Protocol.ACK);
                                ack.setSender(sessionManager.getNome());
                                ack.setMsgId(mensagem.getMsgId());
                                enviar(ack, origem);
                            }
                        });
                    });
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao processar anúncio", e);
            }
        });
    }

    private void processarAck(Message mensagem) {
        Log.d(TAG, "Entrega confirmada: " + mensagem.getMsgId());
    }

    private void enviar(Message mensagem, InetAddress destino) {
        String json = JsonConverter.toJson(mensagem);
        udpCliente.enviar(json, destino);
    }
}
