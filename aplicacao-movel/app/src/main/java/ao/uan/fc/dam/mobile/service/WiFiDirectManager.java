package ao.uan.fc.dam.mobile.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Gestor para comunicação via WiFi Direct (P2P).
 * Responsável por gerir a descoberta, ligação e estado da rede sem fios direta.
 */
public class WiFiDirectManager {
    private static final String TAG = "WiFiDirectManager";

    private final Context context;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final WiFiDirectReceiver receiver;
    private final IntentFilter intentFilter;
    
    private WifiDirectListener listener;
    private final List<WifiP2pDevice> peers = new ArrayList<>();

    public interface WifiDirectListener {
        void onPeersAvailable(Collection<WifiP2pDevice> peerList);
        void onConnectionInfoAvailable(WifiP2pInfo info);
        void onWifiDirectStatusChanged(boolean enabled);
        void onDisconnected();
    }

    public WiFiDirectManager(Context context, WifiDirectListener listener) {
        this.context = context;
        this.listener = listener;
        
        // 1. Inicializar WiFi Direct
        this.manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = manager.initialize(context, Looper.getMainLooper(), null);
        
        this.receiver = new WiFiDirectReceiver();
        this.intentFilter = new IntentFilter();
        setupIntentFilter();
    }

    private void setupIntentFilter() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * Regista o receptor de eventos. Deve ser chamado no onResume da Activity/Fragment.
     */
    public void registerReceiver() {
        context.registerReceiver(receiver, intentFilter);
    }

    /**
     * Remove o registo do receptor. Deve ser chamado no onPause.
     */
    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
    }

    /**
     * Inicia o processo de descoberta de dispositivos próximos.
     */
    @SuppressLint("MissingPermission")
    public void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "P2P: Descoberta iniciada.");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "P2P: Falha na descoberta: " + reason);
            }
        });
    }

    /**
     * Tenta estabelecer uma ligação com um dispositivo específico.
     * @param device O dispositivo remoto ao qual ligar.
     */
    @SuppressLint("MissingPermission")
    public void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "P2P: Ligação iniciada com " + device.deviceName);
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "P2P: Falha ao ligar: " + reason);
            }
        });
    }

    /**
     * Solicita a lista atualizada de peers descobertos.
     */
    @SuppressLint("MissingPermission")
    private void requestPeers() {
        manager.requestPeers(channel, peerList -> {
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            if (listener != null) {
                listener.onPeersAvailable(peers);
            }
        });
    }

    /**
     * Solicita informações sobre a ligação atual (IP do Group Owner, etc).
     */
    private void requestConnectionInfo() {
        manager.requestConnectionInfo(channel, info -> {
            if (listener != null) {
                listener.onConnectionInfoAvailable(info);
            }
        });
    }

    /**
     * Receiver interno para capturar eventos do sistema relativos ao WiFi Direct.
     */
    private class WiFiDirectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (listener != null) {
                    listener.onWifiDirectStatusChanged(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                requestPeers();
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    requestConnectionInfo();
                } else {
                    if (listener != null) listener.onDisconnected();
                }
            }
        }
    }
}
