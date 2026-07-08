package com.uan.anunciosloc.infrastructura_server.util;

public class HaversineUtil {

    private static final double RAIO_TERRA_METROS = 6_371_000.0;

    public static double calcularDistancia(double lat1, double lon1,
            double lat2, double lon2) {
        // Converter graus para radianos
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        // Fórmula de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_METROS * c;
    }

    public static boolean estaDentroDoRaio(double latUtilizador, double lonUtilizador,
            double latInfra, double lonInfra,
            double raioMetros) {
        double distancia = calcularDistancia(latUtilizador, lonUtilizador, latInfra, lonInfra);
        return distancia <= raioMetros;
    }
}
