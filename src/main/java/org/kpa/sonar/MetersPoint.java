package org.kpa.sonar;

import java.math.BigDecimal;

public class MetersPoint implements IPoint {
    private final BigDecimal longitudeMeters;
    private final BigDecimal lattitudeMeters;
    private final BigDecimal depth;

    public MetersPoint(double longitudeMeters, double lattitudeMeters, double depth) {
        this.longitudeMeters = BigDecimal.valueOf(longitudeMeters);
        this.lattitudeMeters = BigDecimal.valueOf(lattitudeMeters);
        this.depth = BigDecimal.valueOf(depth);
    }

    @Override
    public BigDecimal getLongitude() {
        throw new UnsupportedOperationException("MetersPoint");
    }

    @Override
    public BigDecimal getLattitude() {
        throw new UnsupportedOperationException("MetersPoint");
    }

    @Override
    public BigDecimal getDepth() {
        return depth;
    }

    @Override
    public BigDecimal getTemp() {
        throw new UnsupportedOperationException("MetersPoint");
    }

    @Override
    public BigDecimal getLongitudeMeters() {
        return longitudeMeters;
    }

    @Override
    public BigDecimal getLattitudeMeters() {
        return lattitudeMeters;
    }

    @Override
    public double distMeters(IPoint toPoint) {
        return 0;
    }

    @Override
    public String toString() {
        return "MetersPoint{" +
                "longitudeMeters=" + longitudeMeters +
                ", lattitudeMeters=" + lattitudeMeters +
                ", depth=" + depth +
                '}';
    }
}
