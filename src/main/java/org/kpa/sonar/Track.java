package org.kpa.sonar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Track {
    private final String name;
    private final Collection<TrackPoint> points = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger();

    public Track(String name) {
        this.name = name;
    }

    public void addPoint(IPoint point) {
        addPoint(point.getLongitude(), point.getLattitude(), point.getDepth(), point.getTemp());
    }
    public void addPoint(double longitude, double lattitude, double depth, double temp) {
        addPoint(new BigDecimal(longitude), new BigDecimal(lattitude),
                new BigDecimal(depth), new BigDecimal(temp));
    }

    public void addPoint(BigDecimal longitude, BigDecimal lattitude, BigDecimal depth, BigDecimal temp) {
        if (depth == null) {
            return;
        }
        points.add(new TrackPoint(longitude, lattitude, depth, temp, counter.getAndIncrement()));
    }

    public Collection<TrackPoint> getPoints() {
        return Collections.unmodifiableCollection(points);
    }

    public List<TrackPoint> getPoints(double stepInMeters) {
        int counter = 0;
        List<TrackPoint> points = new ArrayList<>(this.points.size());
        TrackPoint lastPoint = null;
        for (TrackPoint point : this.points) {
            if (point.getDepth() == null) {
                continue;
            }
            if (lastPoint != null) {
                int numberOfInterSteps = (int) (lastPoint.distMeters(point) / stepInMeters);
                if (numberOfInterSteps > 1) {
                    List<BigDecimal> longs = fill(lastPoint.getLongitude(), point.getLongitude(), numberOfInterSteps);
                    List<BigDecimal> lats = fill(lastPoint.getLattitude(), point.getLattitude(), numberOfInterSteps);
                    List<BigDecimal> depts = fill(lastPoint.getDepth(), point.getDepth(), numberOfInterSteps);
                    for (int i = 0; i < longs.size(); i++) {
                        points.add(new TrackPoint(
                                longs.get(i),
                                lats.get(i),
                                depts.get(i),
                                point.getTemp(),
                                counter++
                        ));
                    }
                }
            }
            points.add(point);
            lastPoint = point;
        }
        return Collections.unmodifiableList(points);
    }

    public static List<BigDecimal> fill(BigDecimal from, BigDecimal to, int number) {
        List<BigDecimal> values = new ArrayList<>(number);
        BigDecimal step = to.subtract(from).divide(BigDecimal.valueOf(number - 1), 8, BigDecimal.ROUND_HALF_EVEN);
        for (int i = 0; i < number; i++) {
            values.add(from.add(step.multiply(BigDecimal.valueOf(i))));
        }
        return values;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", points(items)=" + points.size() +
                '}';
    }
}
