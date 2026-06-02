package ao.uan.fc.dam.mobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import ao.uan.fc.dam.mobile.service.AnunciosSyncService;

/* JADX INFO: loaded from: classes6.dex */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (temInternet(context)) {
            context.startService(new Intent(context, (Class<?>) AnunciosSyncService.class));
        }
    }

    private boolean temInternet(Context context) {
        Network network;
        NetworkCapabilities capabilities;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        return (manager == null || (network = manager.getActiveNetwork()) == null || (capabilities = manager.getNetworkCapabilities(network)) == null || !capabilities.hasCapability(12)) ? false : true;
    }
}