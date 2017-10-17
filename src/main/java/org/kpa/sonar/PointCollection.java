package org.kpa.sonar;

import org.kpa.game.Point3d;
import org.kpa.sonar.map.Distance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PointCollection {
    private List<IPoint> points = new ArrayList<>();
    private Point swPoint = new Point();
    private Point centerTopPoint = new Point();
    private Point nePoint = new Point();

    public PointCollection() {
    }

    public void add(IPoint point) {
        ((Point) point).setRootPoint(centerTopPoint);
        this.points.add(point);
        update();
    }

    public void addAll(Collection<? extends IPoint> points) {
        points.forEach(point -> ((Point) point).setRootPoint(centerTopPoint));
        this.points.addAll(points);
        update();
    }

    private static BigDecimal min(BigDecimal accumulator, BigDecimal val) {
        return accumulator == null ? val : accumulator.min(val);
    }

    private static BigDecimal max(BigDecimal accumulator, BigDecimal val) {
        return accumulator == null ? val : accumulator.max(val);
    }

    public List<IPoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public void update() {
        BigDecimal minLong = null;
        BigDecimal maxLong = null;
        BigDecimal minLat = null;
        BigDecimal maxLat = null;
        BigDecimal minDepth = null;
        BigDecimal maxDepth = null;
        for (IPoint point : points) {
            maxLong = max(maxLong, point.getLongitude());
            minLong = min(minLong, point.getLongitude());
            maxLat = max(maxLat, point.getLattitude());
            minLat = min(minLat, point.getLattitude());
            maxDepth = max(maxDepth, point.getDepth());
            minDepth = min(minDepth, point.getDepth());
        }
        swPoint.setLattitude(minLat);
        swPoint.setLongitude(minLong);
        swPoint.setDepth(BigDecimal.ZERO);
        swPoint.setRootPoint(swPoint);

        nePoint.setLattitude(maxLat);
        nePoint.setLongitude(maxLong);
        nePoint.setDepth(maxDepth);
        nePoint.setRootPoint(swPoint);

        centerTopPoint.setLongitude(maxLong.add(minLong).divide(BigDecimal.valueOf(2)));
        centerTopPoint.setLattitude(maxLat.add(minLat).divide(BigDecimal.valueOf(2)));
        centerTopPoint.setDepth(BigDecimal.ZERO);
    }

    public List<Point3d> to3d() {
        return points.stream()
                .filter(val -> val.getDepth() != null)
                .map(val -> new Point3d(
                        val.getLongitudeMeters().floatValue(),
                        -val.getDepth().floatValue(),
                        -val.getLattitudeMeters().floatValue())
                ).collect(Collectors.toList());
    }

    public Surface getCoords() {
        return new Surface(to3d());
    }

    private double getTenzor(double maxVal) {
//        return maxVal / getMaxDistance();
        return 1.;
    }

    public double widthMeters() {
        return Distance.dist(
                nePoint.getLattitude().doubleValue(), nePoint.getLongitude().doubleValue(),
                nePoint.getLattitude().doubleValue(), swPoint.getLongitude().doubleValue()
        );
    }

    public double heightMeters() {
        return Distance.dist(
                swPoint.getLattitude().doubleValue(), swPoint.getLongitude().doubleValue(),
                nePoint.getLattitude().doubleValue(), swPoint.getLongitude().doubleValue()
        );
    }

    private double getMaxDistance() {
        return Math.max(heightMeters(), widthMeters());
    }

    public Point getSwPoint() {
        return swPoint;
    }

    public Point getNePoint() {
        return nePoint;
    }

    public int proposeMapSizeSquareMeters() {
        double maxDistance = getMaxDistance() / 2;
        double n = Math.log(maxDistance) / Math.log(2);
        return (int) Math.pow(2, Math.ceil(n));
    }


}
