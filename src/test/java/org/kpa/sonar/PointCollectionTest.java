package org.kpa.sonar;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PointCollectionTest {
    @Test
    public void doTestCollection() {
        PointCollection collection = new PointCollection();
        collection.add(new Point(37, 57, 1));
        collection.add(new Point(36, 56, 1));
        collection.add(new Point(35, 55, 1));
        assertTrue(collection.getPoints().get(2).getLattitudeMeters().doubleValue() < 0);
        assertTrue(collection.getPoints().get(2).getLongitudeMeters().doubleValue() < 0);
        assertTrue(collection.getPoints().get(0).getLattitudeMeters().doubleValue() > 0);
        assertTrue(collection.getPoints().get(0).getLongitudeMeters().doubleValue() > 0);
    }

}