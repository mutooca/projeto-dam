package ao.uan.fc.dam.mobile.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.activity.DashboardActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Arquiteto: Serviço de Sincronização e Notificações (F2.1.3)
 * Responsável por detectar anúncios no local do utilizador e disparar notificações.
 */
public class AnunciosSyncService extends Service {
    private static final String TAG = "AnunciosSyncService";
    private static final String CHANNEL_ID = "AnunciosLocNotifications";
    private static final int NOTIF_STICKY_ID = 1001;
    
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private WifiManager wifiManager;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        createNotificationChannel();
        setupLocationTracking();
    }

    private void setupLocationTracking() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) syncWithServer(location);
            }
        };
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
                .setMinUpdateIntervalMillis(15000)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            Log.e(TAG, "Erro permissão");
        }
    }

    private void syncWithServer(Location location) {
        String email = SessionManager.getEmail(this);
        if (email == null) { stopSelf(); return; }

        List<String> wifiIds = getVisibleWifiIds();
        Map<String, Object> syncData = new HashMap<>();
        syncData.put("email", email);
        syncData.put("latitude", location.getLatitude());
        syncData.put("longitude", location.getLongitude());
        syncData.put("wifi_ids", wifiIds);

        RetrofitClient.getInstance().getApi().anunciarLocalizacao(syncData).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(@NonNull Call<List<Anuncio>> call, @NonNull Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (Anuncio a : response.body()) {
                        showNewAnuncioNotification(a);
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<List<Anuncio>> call, @NonNull Throwable t) {}
        });
    }

    private List<String> getVisibleWifiIds() {
        List<String> ssids = new ArrayList<>();
        try {
            List<ScanResult> results = wifiManager.getScanResults();
            for (ScanResult res : results) {
                if (res.SSID != null && !res.SSID.isEmpty()) ssids.add(res.SSID);
            }
        } catch (Exception e) {}
        return ssids;
    }

    private void showNewAnuncioNotification(Anuncio anuncio) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        // Criar Intent para abrir o detalhe do anúncio na DashboardActivity
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("OPEN_ANUNCIO", anuncio);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pi = PendingIntent.getActivity(this, anuncio.getIdAnuncio().hashCode(), 
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Novo anúncio em " + (anuncio.getNome_local() != null ? anuncio.getNome_local() : "seu local"))
                .setContentText(anuncio.getTitulo())
                .setSubText("Toque para ler")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        nm.notify(anuncio.getIdAnuncio().hashCode(), n);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_STICKY_ID, getStickyNotification());
        startLocationUpdates();
        return START_STICKY;
    }

    private Notification getStickyNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AnunciosLoc")
                .setContentText("A monitorizar novos anúncios no local...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Anúncios Locais",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
