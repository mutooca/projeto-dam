package ao.uan.fc.dam.mobile.service;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Neighbor;

public class DecentralizedManager {
    private static final String TAG = "DecentralizedManager";
    private final Context context;
    private final DiscoveryManager discoveryManager;
    private final AdvertisementManager advertisementManager;
    private final UdpServer udpServer;
    private boolean isRunning = false;
    private final String myEmail;

    public DecentralizedManager(Context context, String myEmail) {
        this.context = context;
        this.myEmail = myEmail;
        this.discoveryManager = new DiscoveryManager(context, myEmail);
        this.advertisementManager = new AdvertisementManager(context, myEmail);
        this.udpServer = new UdpServer(discoveryManager, advertisementManager);
    }

    public void start() {
        if (!isRunning) {
            udpServer.start();
            isRunning = true;
            Log.i(TAG, "Serviço Descentralizado P2P Iniciado para " + myEmail);
        }
    }

    /**
     * Tenta enviar anúncios para todos os vizinhos conhecidos que tenham perfil compatível.
     */
    public void syncAdsWithNeighbors() {
        new Thread(() -> {
            // Obter todos os anúncios locais da Room
            List<Anuncio> todosAnuncios = AppDatabase.getInstance(context).anuncioDao().getAll();
            Map<String, Neighbor> neighbors = discoveryManager.getNeighborMap();
            Map<String, Map<String, String>> profiles = discoveryManager.getNeighborProfiles();

            Log.d(TAG, "Iniciando verificação de envio P2P para " + neighbors.size() + " vizinhos. Total de anúncios na Room: " + todosAnuncios.size());

            for (String nodeId : neighbors.keySet()) {
                Neighbor n = neighbors.get(nodeId);
                Map<String, String> profile = profiles.get(nodeId);

                if (profile == null) {
                    Log.w(TAG, "Perfil do vizinho " + nodeId + " ainda não foi recebido. Pulando.");
                    continue;
                }

                for (Anuncio ad : todosAnuncios) {
                    // Evitar reenvio de anúncios criados pelo próprio destinatário
                    if (nodeId.equalsIgnoreCase(ad.getAutorEmail())) {
                        continue;
                    }

                    // Parse do campo restricaoPerfil (ex: "interesse=leitura")
                    Map<String, String> restrictionMap = parseRestricao(ad.getRestricaoPerfil());
                    Map<String, String> whitelist = null;
                    Map<String, String> blacklist = null;

                    if ("BLACKLIST".equalsIgnoreCase(ad.getTipoPolitica())) {
                        blacklist = restrictionMap;
                    } else {
                        // Padrão ou explicitamente WHITELIST
                        whitelist = restrictionMap;
                    }

                    // Executar correspondência real usando a classe PolicyMatcher
                    boolean isMatch = PolicyMatcher.match(profile, whitelist, blacklist);
                    Log.d(TAG, "Verificando anúncio [" + ad.getTitulo() + "] para vizinho [" + nodeId + "]. Match=" + isMatch);

                    if (isMatch) {
                        Log.i(TAG, "Match de política confirmado! Enviando anúncio via WiFi Direct para: " + n.getIpAddress());
                        // Altera o estado do anúncio recebido no outro lado para diferenciar
                        ad.setModo_entrega("DESCENTRALIZADO");
                        advertisementManager.sendAdvertisement(n.getIpAddress(), ad);
                    }
                }
            }
        }).start();
    }

    private Map<String, String> parseRestricao(String restricao) {
        Map<String, String> map = new HashMap<>();
        if (restricao != null && restricao.contains("=")) {
            String[] parts = restricao.split("=");
            if (parts.length == 2) {
                map.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }
        return map;
    }

    public void startDiscovery(String ip) {
        discoveryManager.startDiscovery(ip);
    }

    public void stop() {
        udpServer.stopServer();
        isRunning = false;
    }
}
