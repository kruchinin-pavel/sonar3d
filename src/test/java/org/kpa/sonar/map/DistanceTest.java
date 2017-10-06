package org.kpa.sonar.map;

import org.junit.Test;
import org.kpa.sonar.Point;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class DistanceTest {
    @Test
    public void testDistance() {
        BigDecimal dist = Distance.dist(
                new Point(57.2571191, 37.8608742, 5.97),
                new Point(55.9792038, 42.8180718, 6.06));
        System.out.println(dist);

        dist = Distance.dist(
                new Point(57.2571191, 37.8608742, 5.97),
                new Point(57.2252676, 37.7390804, 6.06));
        System.out.println(dist);

    }

    @Test
    public void testRelativeMeters() {

        List<Point> points = Arrays.asList(
                new Point(57.2571191, 37.8608742, 0),
                new Point(57.2573000, 37.8608742, 0),
                new Point(57.2252676, 37.7390804, 0));

        List<Point> relativePoints = Distance.toRelativeMeters(points);
        System.out.println(relativePoints);
    }

}