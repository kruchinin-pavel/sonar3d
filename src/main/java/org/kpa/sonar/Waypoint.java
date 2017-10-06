package org.kpa.sonar;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Waypoint extends Point {
    private final LocalDateTime dt;
    private final String name;

    public Waypoint(String name, LocalDateTime dt, BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp) {
        super(longitude, lattitude, depth, temp);
        this.dt = dt;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "dt=" + dt +
                ", name='" + name + '\'' +
                ", longitude=" + getLongitude() +
                ", lattitude=" + getLattitude() +
                ", depth=" + getDepth() +
                ", temp=" + getTemp() +
                '}';
    }
}
