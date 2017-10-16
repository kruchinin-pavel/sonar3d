package org.kpa.sonar;

import com.google.common.base.Preconditions;
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
        int index = 0;
        for (int z = -gridSize / 2; z <= gridSize / 2; z++) {
            row[index++] = z * 2;
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
        private final int zIndex;
        private final float zVal;
        private double[] xRow;
        private final Surface coords;

        public InterpY(float[] values, int zIndex, float zVal, double[] xRow, Surface coords) {
            this.values = values;
            this.zIndex = zIndex;
            this.zVal = zVal;
            this.xRow = xRow;
            this.coords = coords;
        }

        public void makeYRow() {
            int index = 0;
            for (double xVal : xRow) {
                double[] p = new double[]{xVal, zVal};
                double res = sphere.value(p, coords.getPts(), coords.getVals(), 1., 1.);
                values[xRow.length * zIndex + index] = (float) res;
                index++;
            }
        }
    }


    public float[] buildHeights() {
        if (isGrid()) {
            return getDirectGrid();
        } else {
            return interpolateGrid(proposeMapSizeSquareMeters());
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
        Preconditions.checkArgument(((gridFacetSize & -gridFacetSize) == gridFacetSize),"Not a power of two");
        List<Point3d> pts = new ArrayList<>();
        double[] doubles = generateRow(gridFacetSize);
        for (double z : doubles) {
            for (double x : doubles) {
                pts.add(new Point3d(x, yFunction.apply(x, z), z));
            }
        }
        return new Surface(pts, gridFacetSize);
    }

    public Surface fillBounds() {
        if (isGrid()) {
            return this;
        }
        double[] doubles = generateRow(proposeMapSizeSquareMeters());
        List<Point3d> points = new ArrayList<>();
        forEach(points::add);
        double minV = doubles[0];
        double maxV = doubles[doubles.length - 1];
        for (double v : doubles) {
            points.add(new Point3d(minV, 0, v));
            points.add(new Point3d(maxV, 0, v));
            points.add(new Point3d(v, 0, minV));
            points.add(new Point3d(v, 0, maxV));
        }
        return new Surface(points, -1);
    }
}
