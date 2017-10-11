package org.kpa.sonar;

import com.google.common.base.Preconditions;
import org.kpa.sonar.map.Distance;

import java.math.BigDecimal;

import static org.kpa.sonar.map.Distance.dist;

public class Point implements IPoint {
    private BigDecimal longitude;
    private BigDecimal lattitude;
    private BigDecimal depth;
    private BigDecimal temp;
    private IPoint rootPoint;

    public Point() {
    }

    public Point(double longitude, double lattitude, double depth) {
        this(BigDecimal.valueOf(longitude), BigDecimal.valueOf(lattitude), BigDecimal.valueOf(depth), BigDecimal.ZERO);
    }

    public Point(BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp) {
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.depth = depth;
        this.temp = temp;
    }

    @Override
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public BigDecimal getLattitude() {
        return lattitude;
    }

    public void setLattitude(BigDecimal lattitude) {
        this.lattitude = lattitude;
    }

    @Override
    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    @Override
    public BigDecimal getTemp() {
        return temp;
    }

    public void setTemp(BigDecimal temp) {
        this.temp = temp;
    }

    @Override
    public BigDecimal getLongitudeMeters() {
        Preconditions.checkNotNull(rootPoint);
        BigDecimal longDist = new BigDecimal(dist(getLattitude().doubleValue(), rootPoint.getLongitude().doubleValue(),
                getLattitude().doubleValue(), getLongitude().doubleValue()));
        return rootPoint.getLongitude().compareTo(getLongitude()) < 0 ? longDist : longDist.negate();
    }

    @Override
    public BigDecimal getLattitudeMeters() {
        Preconditions.checkNotNull(rootPoint);
        BigDecimal latDist = new BigDecimal(dist(
                rootPoint.getLattitude().floatValue(), getLongitude().doubleValue(),
                getLattitude().doubleValue(), getLongitude().doubleValue()));
        return rootPoint.getLattitude().compareTo(getLattitude()) < 0 ? latDist : latDist.negate();
    }

    public IPoint getRootPoint() {
        return rootPoint;
    }

    public void setRootPoint(IPoint rootPoint) {
        this.rootPoint = rootPoint;
    }

    @Override
    public String toString() {
        return "Point{lat,lon(deg)=" + lattitude + ", " + longitude + ", depth(m)=" + depth + '}';
    }

    @Override
    public double distMeters(IPoint toPoint) {
        return Distance.dist(this, toPoint).doubleValue();
    }
}
