package org.kpa.sonar;

import org.junit.Test;
import org.kpa.sonar.map.Interpolator;

import static org.junit.Assert.assertEquals;

public class SurfaceTest {
    @Test
    public void doBuildHeightsTest() {
        Surface coords = Interpolator.generateSin(32);
        float[] heights = coords.buildHeights(32);
        for (int x = 0; x <= 32; x++) {
            for (int y = 0; y <= 32; y++) {
                assertEquals("" + x + ":" + y, heights[x * 33 + y], coords.getVals()[x * 33 + y], 1e-6);
//                assertEquals(x,coords.getPts()[x][0],1e-6);
//                assertEquals(x,coords.getPts()[x][0],1e-6);
            }
        }
    }

}