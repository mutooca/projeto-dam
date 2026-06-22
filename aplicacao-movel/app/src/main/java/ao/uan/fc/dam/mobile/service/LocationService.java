package ao.uan.fc.dam.mobile.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

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
 * Arquiteto: Serviço de Localização em Foreground (F2.1.4)
 * Responsável por rastreamento contínuo e sincronização com o servidor central.
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "LocationUpdates";
    private static final int NOTIF_ID = 12345;
    
    // Configuração do intervalo (Requisito: 30 segundos)
    private static final long UPDATE_INTERVAL = 30000; 
    private static final long FASTEST_INTERVAL = 15000;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
        setupLocationCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Serviço iniciado...");
        
        // Verifica se a sessão existe antes de rodar
        if (!SessionManager.hasSession(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        startForeground(NOTIF_ID, getNotification("Rastreamento ativo", "A buscar anúncios próximos..."));
        requestLocationUpdates();
        
        return START_STICKY; // Mantém ativo mesmo se o sistema matar a app
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (Exception e) {
            Log.e(TAG, "Erro ao solicitar atualizações", e);
        }
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    syncWithServer(location);
                }
            }
        };
    }

    private void syncWithServer(Location location) {
        String email = SessionManager.getEmail(this);
        if (email == null) {
            stopSelf();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("latitude", location.getLatitude());
        data.put("longitude", location.getLongitude());

        Log.d(TAG, "Sincronizando: " + location.getLatitude() + ", " + location.getLongitude());

        RetrofitClient.getInstance().getApi().anunciarLocalizacao(data).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    notifyNewAds(response.body().size());
                }
            }
            @Override public void onFailure(Call<List<Anuncio>> call, Throwable t) {
                Log.e(TAG, "Falha na sincronização", t);
            }
        });
    }

    private void notifyNewAds(int count) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, getNotification("Novos anúncios!", "Existem " + count + " mensagens novas para si."));
    }

    private Notification getNotification(String title, String content) {
        Intent intent = new Intent(this, DashboardActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Serviço de Localização",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        Log.d(TAG, "Serviço encerrado.");
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
