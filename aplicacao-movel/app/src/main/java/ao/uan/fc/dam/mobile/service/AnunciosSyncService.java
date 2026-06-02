package ao.uan.fc.dam.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;

public class AnunciosSyncService extends Service {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executor.execute(() -> {
            String email = KerberosAuthManager.getEmail(getApplicationContext());
            if (email != null) {
                try {
                    RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).execute();
                } catch (Exception ignored) {
                }
            }
            stopSelf(startId);
        });
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
