package org.kpa.sonar.map;

import org.kpa.sonar.IPoint;

import java.math.BigDecimal;

public class Distance {
    public static BigDecimal dist(IPoint p1, IPoint p2) {
        return BigDecimal.valueOf(dist(
                p1.getLattitude().floatValue(),
                p1.getLongitude().floatValue(),
                p2.getLattitude().floatValue(),
                p2.getLongitude().floatValue()));
    }

    public static double dist(double lat1, double lng1, double lat2, double lng2) {
//        double earthRadius = 6371000; //meters
        double earthRadius = 6371000 * 335 / 561; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


}
