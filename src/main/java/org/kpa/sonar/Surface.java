package org.kpa.sonar;

import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.kpa.game.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Surface {
    private final double[][] pts;
    private final double[] vals;
    private final double minX, minY, maxX, maxY;
    private static final Logger logger = LoggerFactory.getLogger(Surface.class);
    private final int gridSize;

    public Surface(List<Point3d> points, int gridSize) {
        pts = new double[points.size()][2];
        vals = new double[points.size()];
        this.gridSize = gridSize;
        double minX = 0, minZ = 0, maxX = 0, maxZ = 0;
        int index = 0;
        for (Point3d point : points) {
            double x = point.getX();
            double z = point.getZ();
            pts[index][0] = x;
            pts[index][1] = z;
            vals[index] = point.getY();
            if (index == 0) {
                maxX = minX = x;
                maxZ = minZ = z;
            }
            maxX = Math.max(maxX, x);
            maxZ = Math.max(maxZ, z);
            minX = Math.min(minX, x);
            minZ = Math.min(minZ, z);
            index++;
        }
        this.minX = minX;
        this.minY = minZ;
        this.maxX = maxX;
        this.maxY = maxZ;
    }

    public double getMaxDistance() {
        return Math.max(maxX - minX, maxY - minY);
    }

    public int proposeMapSizeSquareMeters() {
        if (isGrid()) {
            return gridSize;
        } else {
            double maxDistance = getMaxDistance();
            double n = Math.log(maxDistance) / Math.log(2);
            return (int) Math.pow(2, Math.ceil(n));
        }
    }

    public double[][] getPts() {
        return pts;
    }

    public double[] getVals() {
        return vals;
    }


    private static double[] generateRow(int gridSize) {
        double[] row = new double[gridSize + 1];
        for (int i = 0; i <= gridSize; i++) {
            row[i] = -gridSize + (double) i;
        }
        return row;
    }

    public float[] getDirectGrid() {
        float[] vals = new float[this.vals.length];
        for (int index = 0; index < this.vals.length; index++) {
            vals[index] = (float) this.vals[index];
        }
        return vals;
    }

    public boolean isGrid() {
        return gridSize > 0;
    }

    private static class InterpY {
        private final float values[];
        private final InterpolatingMicrosphere sphere =
                new InterpolatingMicrosphere(2, 50, 1, 0, -0,
                        new UnitSphereRandomVectorGenerator(2));
        private final int xIndex;
        private final float xVal;
        private double[] yRow;
        private final Surface coords;

        public InterpY(float[] values, int xIndex, float xVal, double[] yRow, Surface coords) {
            this.values = values;
            this.xIndex = xIndex;
            this.xVal = xVal;
            this.yRow = yRow;
            this.coords = coords;
        }

        public void makeYRow() {
            int index = 0;
            for (double yVal : yRow) {
                double[] p = new double[]{xVal, yVal};
                double res = sphere.value(p, coords.getPts(), coords.getVals(), 1., 1.);
                values[yRow.length * xIndex + index] = (float) res;
                index++;
            }
        }
    }


    public float[] buildHeights(int gridSize) {
        if (isGrid()) {
            return getDirectGrid();
        } else {
            return interpolateGrid(gridSize);
        }
    }

    private float[] interpolateGrid(int gridSize) {
        try {
            float values[] = new float[(gridSize + 1) * (gridSize + 1)];
            double[] row = generateRow(gridSize);
            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            CountDownLatch cdl = new CountDownLatch(gridSize);
            int index = 0;
            for (double xVal : row) {
                final int fIndex = index;
                service.execute(() -> {
                    try {
                        new InterpY(values, fIndex, (float) xVal, row, this).makeYRow();
                    } finally {
                        cdl.countDown();
                        logger.info("Left {} tasks.", cdl.getCount());
                    }
                });
                index++;
            }
            service.shutdown();
            cdl.await();
            return values;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void forEach(Consumer<Point3d> consumer) {
        for (int index = 0; index < vals.length; index++) {
            consumer.accept(new Point3d(pts[index][0], vals[index], pts[index][1]));
        }
    }

    public static Surface generateGrid(int gridFacetSize, BiFunction<Double, Double, Double> yFunction) {
        List<Point3d> pts = new ArrayList<>();
        for (int z = -gridFacetSize / 2; z <= gridFacetSize / 2; z++) {
            for (int x = -gridFacetSize / 2; x <= gridFacetSize / 2; x++) {
                pts.add(new Point3d(x * 2, yFunction.apply((double) x, (double) z), z * 2));
            }
        }
        return new Surface(pts, gridFacetSize);
    }
}
