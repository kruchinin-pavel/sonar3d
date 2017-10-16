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

}