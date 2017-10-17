package org.kpa.sonar;

import org.junit.Test;
import org.kpa.game.Point3d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SurfaceTest {
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParams() {
        Surface.generateGrid(48, SurfaceTest::getHeight);
    }

    @Test
    public void doBuildHeightsTest() {
        Surface coords = Surface.generateGrid(32, SurfaceTest::getHeight);
        float[] heights = coords.buildHeights();
        for (int x = 0; x <= 32; x++) {
            for (int y = 0; y <= 32; y++) {
                assertEquals("" + x + ":" + y, heights[x * 33 + y], coords.getVals()[x * 33 + y], 1e-6);
            }
        }
    }

    @Test
    public void testCompatibility() {
        int gridFacetSize = 16;
        List<Point3d> pts = new ArrayList<>();
        double[] doubles = Surface.generateRow(gridFacetSize);
        for (double x : doubles) {
            for (double z : doubles) {
                pts.add(new Point3d(x, getHeight(x, z), z));
            }
        }
        Surface surfRes = new Surface(pts);
        Surface surfExpected = Surface.generateGrid(gridFacetSize, SurfaceTest::getHeight);

        float[] heightsExp = surfExpected.buildHeights();
        float[] heightsRes = surfRes.buildHeights();
        assertArrayEquals(surfExpected.getVals(), surfRes.getVals(), 0.01);
        assertEquals(heightsExp.length, heightsRes.length);
        assertArrayEquals(heightsExp, heightsRes, 0.01f);
    }

    private static Double getHeight(Double x, Double z) {
        return -10 + Math.sin(x * z);
    }

    @Test
    public void testIO() throws IOException {
        Surface surface = Surface.generateGrid(32, SurfaceTest::getHeight);
        surface.storeToFile("build/surface.txt");

        Surface restored = Surface.readFromFile("build/surface.txt");
        assertArrayEquals(surface.buildHeights(), restored.buildHeights(), 0.01f);
        assertTrue(surface.isGrid());
        assertTrue(restored.isGrid());
    }
}