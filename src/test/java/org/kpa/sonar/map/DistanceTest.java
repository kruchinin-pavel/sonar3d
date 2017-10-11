package org.kpa.sonar.map;

import org.junit.Test;
import org.kpa.sonar.ImmutablePoint;

import java.math.BigDecimal;

public class DistanceTest {
    @Test
    public void testDistance() {
        BigDecimal dist = Distance.dist(
                new ImmutablePoint(57.2571191, 37.8608742, 5.97),
                new ImmutablePoint(55.9792038, 42.8180718, 6.06));
        System.out.println(dist);

        dist = Distance.dist(
                new ImmutablePoint(57.2571191, 37.8608742, 5.97),
                new ImmutablePoint(57.2252676, 37.7390804, 6.06));
        System.out.println(dist);

    }

}