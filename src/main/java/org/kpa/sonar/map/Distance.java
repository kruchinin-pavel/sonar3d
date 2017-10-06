package org.kpa.sonar.map;

import org.kpa.sonar.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Distance {
    public static BigDecimal dist(Point p1, Point p2) {
        return BigDecimal.valueOf(dist(
                p1.getLattitude().floatValue(),
                p1.getLongitude().floatValue(),
                p2.getLattitude().floatValue(),
                p2.getLongitude().floatValue()));
    }

    private static float dist(float lat1, float lng1, float lat2, float lng2) {
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

    public static List<Point> toRelativeMeters(List<Point> points) {
        BigDecimal minLong = null;
        BigDecimal maxLong = null;
        BigDecimal minLat = null;
        BigDecimal maxLat = null;
        for (Point point : points) {
            maxLong = max(maxLong, point.getLongitude());
            minLong = min(minLong, point.getLongitude());
            maxLat = max(maxLat, point.getLattitude());
            minLat = min(minLat, point.getLattitude());
        }

        List<Point> relativePoints = new ArrayList<>(points.size());
        for (Point point : points) {
            float relativeLongitudeMeters = dist(
                    point.getLattitude().floatValue(), minLong.floatValue(),
                    point.getLattitude().floatValue(), point.getLongitude().floatValue());

            float relativeLattudeMeters = dist(
                    minLat.floatValue(), point.getLongitude().floatValue(),
                    point.getLattitude().floatValue(), point.getLongitude().floatValue());
            Point relativePoint = new Point(
                    relativeLongitudeMeters,
                    relativeLattudeMeters,
                    point.getDepth().floatValue()
            );
            relativePoints.add(relativePoint);
        }
        return relativePoints;
    }

    private static BigDecimal min(BigDecimal accumulator, BigDecimal val) {
        return accumulator == null ? val : accumulator.min(val);
    }

    private static BigDecimal max(BigDecimal accumulator, BigDecimal val) {
        return accumulator == null ? val : accumulator.max(val);
    }
}
