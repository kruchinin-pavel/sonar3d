package org.kpa.sonar;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrackTest {
    @Test
    public void testFill() {
        List<BigDecimal> track = Track.fill(BigDecimal.ONE, BigDecimal.TEN, 10);
        assertEquals(10, track.size());
        assertEquals(1., track.get(0).doubleValue(), 0.01);
        assertEquals(10., track.get(9).doubleValue(), 0.01);
    }

    @Test
    public void testGetVlas() {
        Track track = new Track("Test");
        track.addPoint(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO);
        track.addPoint(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);
        List<TrackPoint> pts = track.getPoints(10);
        assertEquals(10, pts.size());
        assertEquals(1., pts.get(0).getLattitude().doubleValue(), 0.01);
        assertEquals(10., pts.get(9).getLattitude().doubleValue(), 0.01);
    }

}