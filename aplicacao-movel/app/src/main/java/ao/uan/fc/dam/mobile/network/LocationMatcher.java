package ao.uan.fc.dam.mobile.network;

import android.location.Location;
import android.util.Log;

import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;

public class LocationMatcher {

    private static final String TAG = "LocationMatcher";

    public static boolean estaNoLocal(LocalCompleto localTarget, double currentLat, double currentLon, String currentSsid) {
        if (localTarget == null) return false;

        // 1. Verificar WiFi se disponível no local
        CoordenadaWifi wifi = localTarget.getCoordenadaWifi();
        if (wifi != null && wifi.getSsid() != null && !wifi.getSsid().isEmpty()) {
            if (currentSsid != null && currentSsid.equalsIgnoreCase(wifi.getSsid())) {
                Log.d(TAG, "Localização validada via WiFi: " + currentSsid);
                return true;
            }
        }

        // 2. Verificar GPS se disponível no local
        CoordenadaGps gps = localTarget.getCoordenadaGps();
        if (gps != null && !Double.isNaN(currentLat) && !Double.isNaN(currentLon)) {
            float[] results = new float[1];
            Location.distanceBetween(currentLat, currentLon, gps.getLatitude(), gps.getLongitude(), results);
            float distancia = results[0];

            if (distancia <= gps.getRaio()) {
                Log.d(TAG, "Localização validada via GPS. Distância: " + distancia + "m, Raio: " + gps.getRaio() + "m");
                return true;
            } else {
                Log.d(TAG, "Fora do raio GPS. Distância: " + distancia + "m");
            }
        }

        return false;
    }
}
