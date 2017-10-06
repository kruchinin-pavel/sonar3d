package org.kpa.sonar;

import java.math.BigDecimal;

public class TrackPoint extends Point {
    private final int num;

    public TrackPoint(BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp, int num) {
        super(longitude, lattitude, depth, temp);
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "num=" + num +
                ", longitude=" + getLongitude() +
                ", lattitude=" + getLattitude() +
                ", depth=" + getDepth() +
                ", temp=" + getTemp() +
                '}';
    }
}
