package org.kpa.sonar;

import java.math.BigDecimal;

public interface IPoint {
    BigDecimal getLongitude();

    BigDecimal getLattitude();

    BigDecimal getDepth();

    BigDecimal getTemp();

    BigDecimal getLongitudeMeters();

    BigDecimal getLattitudeMeters();

    double distMeters(IPoint toPoint);
}
