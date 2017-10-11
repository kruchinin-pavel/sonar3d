package org.kpa.sonar;

import java.math.BigDecimal;

public class ImmutablePoint extends Point {

    public ImmutablePoint(double longitude, double lattitude, double depth) {
        this(BigDecimal.valueOf(longitude), BigDecimal.valueOf(lattitude), BigDecimal.valueOf(depth), BigDecimal.ZERO);
    }

    public ImmutablePoint(BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp) {
        super(longitude, lattitude, depth, temp);
    }


    @Override
    public void setLongitude(BigDecimal longitude) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public void setLattitude(BigDecimal lattitude) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public void setDepth(BigDecimal depth) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public void setTemp(BigDecimal temp) {
        throw new UnsupportedOperationException("Read only");
    }
}
