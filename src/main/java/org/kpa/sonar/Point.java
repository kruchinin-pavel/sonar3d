package org.kpa.sonar;

import java.math.BigDecimal;

public class Point {
    private final BigDecimal longitude;
    private final BigDecimal lattitude;
    private final BigDecimal depth;
    private final BigDecimal temp;

    public Point(double longitude, double lattitude, double depth) {
        this(BigDecimal.valueOf(longitude), BigDecimal.valueOf(lattitude), BigDecimal.valueOf(depth), BigDecimal.ZERO);
    }

    public Point(BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp) {
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.depth = depth;
        this.temp = temp;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getLattitude() {
        return lattitude;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public BigDecimal getTemp() {
        return temp;
    }

    @Override
    public String toString() {
        return "Point{" +
                "longitude=" + longitude +
                ", lattitude=" + lattitude +
                ", depth=" + depth +
                ", temp=" + temp +
                '}';
    }
}
