package ao.uan.fc.dam.mobile.contentProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import ao.uan.fc.dam.mobile.util.LocalizacaoCallback;

public class LocalizacaoProvider {

    private final FusedLocationProviderClient fusedLocation;
    private final Context context;

    public LocalizacaoProvider(Context context){
        this.context = context.getApplicationContext();
        fusedLocation = LocationServices.getFusedLocationProviderClient(this.context);
    }

    @SuppressLint("MissingPermission")
    public void obterLocalizacao(LocalizacaoCallback callback){
        fusedLocation.getLastLocation()
                .addOnSuccessListener(location -> {
                    if(location != null){
                        callback.onResultado(
                                location.getLatitude(),
                                location.getLongitude());
                    }else{
                        Log.e("GPS", "Localização retornou null");
                        callback.onResultado(Double.NaN, Double.NaN);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GPS", "Erro ao obter localização", e);
                    callback.onResultado(Double.NaN, Double.NaN);
                });

    }

    public String obterSsid() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            String ssid = info.getSSID();
            if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            return ssid;
        }
        return null;
    }

    public boolean possuiPermissao(Context context){

        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

    }

}
