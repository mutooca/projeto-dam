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
    private static final long INTERVALO_DESCOBERTA = 20000; // 20 segundos

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

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean autoDescobertaActiva = false;

    private final Runnable discoveryRunnable = new Runnable() {
        @Override
        public void run() {
            if (autoDescobertaActiva && !ligado && !aLigar) {
                descobrirPeers();
            }
            if (autoDescobertaActiva) {
                handler.postDelayed(this, INTERVALO_DESCOBERTA);
            }
        }
    };

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

    public void iniciarAutonomo() {
        registar();
        autoDescobertaActiva = true;
        handler.removeCallbacks(discoveryRunnable);
        handler.post(discoveryRunnable);
        Log.d(TAG, "Modo autónomo iniciado");
    }

    public void pararAutonomo() {
        autoDescobertaActiva = false;
        handler.removeCallbacks(discoveryRunnable);
        desconectar();
        cancelarRegistro();
        Log.d(TAG, "Modo autónomo parado");
    }

    public void registar() {
        if (registado) return;

        receiver = new BroadcastReceiver() {
            @Override
            @SuppressLint("MissingPermission")
            public void onReceive(Context ctx, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;

                switch (action) {
                    case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                        int estado = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                        if (estado != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                            Log.w(TAG, "WiFi Direct desativado");
                            listener.onWifiDirectIndisponivel();
                        }
                        break;

                    case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                        WifiP2pDevice thisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                        if (thisDevice != null) {
                            enderecoLocal = thisDevice.deviceAddress;
                        }
                        break;

                    case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                        if (temPermissoes()) {
                            manager.requestPeers(channel, peerList -> {
                                List<WifiP2pDevice> peers = new ArrayList<>(peerList.getDeviceList());
                                Log.d(TAG, "Peers detectados: " + peers.size());
                                listener.onPeersDisponiveis(peers);
                            });
                        }
                        break;

                    case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                        if (temPermissoes()) {
                            manager.requestConnectionInfo(channel, info -> {
                                if (info.groupFormed) {
                                    ligado = true;
                                    aLigar = false;
                                    String ipPeer = info.isGroupOwner ? "192.168.49.2" : info.groupOwnerAddress.getHostAddress();
                                    
                                    try {
                                        Peer peer = new Peer("WiFiDirect", java.net.InetAddress.getByName(ipPeer), 8888);
                                        PeerManager.adicionar(peer);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Erro ao processar IP", e);
                                    }
                                    listener.onLigacaoEstabelecida(info);
                                } else {
                                    ligado = false;
                                    // Se a ligação caiu, reinicia descoberta se autónomo
                                    if (autoDescobertaActiva && !aLigar) {
                                        descobrirPeers();
                                    }
                                }
                            });
                        }
                        break;
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(receiver, intentFilter);
        }
        registado = true;
    }

    public void cancelarRegistro() {
        if (registado && receiver != null) {
            context.unregisterReceiver(receiver);
            registado = false;
        }
    }

    @SuppressLint("MissingPermission")
    public void descobrirPeers() {
        if (!temPermissoes()) return;

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Descoberta automática iniciada");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "Falha na descoberta: " + reasonCode);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void conectarComDesempate(WifiP2pDevice device) {
        if (!temPermissoes() || aLigar || ligado) return;

        if (enderecoLocal == null || enderecoLocal.equals("02:00:00:00:00:00")) {
            return;
        }
        
        // Estratégia de desempate para evitar conflitos de conexão simultânea
        if (enderecoLocal.compareTo(device.deviceAddress) >= 0) {
            return;
        }

        aLigar = true;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Convite enviado para: " + device.deviceName);
            }

            @Override
            public void onFailure(int reason) {
                aLigar = false;
                Log.e(TAG, "Erro ao conectar: " + reason);
            }
        });
    }

    public void desconectar() {
        ligado = false;
        aLigar = false;
        manager.removeGroup(channel, null);
    }

    private boolean temPermissoes() {
        boolean localizacao = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean nearby = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            nearby = ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED;
        }
        return localizacao && nearby;
    }
}