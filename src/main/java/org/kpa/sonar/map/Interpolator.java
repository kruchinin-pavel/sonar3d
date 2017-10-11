package org.kpa.sonar.map;

import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.kpa.sonar.Coords;
import org.kpa.sonar.ImmutablePoint;
import org.kpa.util.ChartBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Interpolator {
    private static final Logger logger = LoggerFactory.getLogger(Interpolator.class);


    public static void main(String[] args) {
        test2d();
    }

    private static class InterpY {
        private final float values[];
        private final InterpolatingMicrosphere sphere =
                new InterpolatingMicrosphere(2, 50, 1, 0, -0,
                new UnitSphereRandomVectorGenerator(2));
        private final int xIndex;
        private final float xVal;
        private double[] yRow;
        private final Coords coords;

        public InterpY(float[] values, int xIndex, float xVal, double[] yRow, Coords coords) {
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

    private static double[] generateRow(int gridSize) {
        double[] row = new double[gridSize * 2];
        for (int i = 0; i < gridSize * 2; i++) {
            row[i] = -gridSize + (double) i;
        }
        return row;
    }

    public static float[] buildHeights(int gridSize, Coords coords) {
        try {
            float values[] = new float[gridSize * gridSize * 4];
            double[] row = generateRow(gridSize);

            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            CountDownLatch cdl = new CountDownLatch(gridSize);
            int index = 0;
            for (double xVal : row) {
                final int fIndex = index;
                service.execute(() -> {
                    try {
                        InterpY interpY = new InterpY(values, fIndex, (float) xVal, row, coords);
                        interpY.makeYRow();
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

    public static float[] test3d(int points, double maxVal) {
        return test3d(points, maxVal, false);
    }

    public static float[] test3d(int points, double maxVal, boolean print) {
        double scale = maxVal / 10.;
        double max = 10;
        final int DIMENSION = 2;
        InterpolatingMicrosphere val = new InterpolatingMicrosphere(DIMENSION, 50, 1, .1, -100,
                new UnitSphereRandomVectorGenerator(DIMENSION));

        double mid = max / 2;
        double[][] samplePoints = new double[][]{
                {0., 0.}, {0., mid}, {0., max},
                {mid, 0.}, {mid, mid}, {mid, max},
                {max, 0.}, {max, mid}, {max, max}
        };
        double[] sampleVals = new double[]{
                0., mid, max,
                0., mid, max,
                0., mid, max,
        };
        float[] values = new float[points * points];
        int index = 0;
        for (int xi = 0; xi < points; xi++) {
            for (int yi = 0; yi < points; yi++) {
                double[] p = new double[]{(double) xi / points * max, (double) yi / points * max};
                double res = val.value(p, samplePoints, sampleVals, 1., 1.) * scale;
                if (print) System.out.println(String.format("%s\t%s", Arrays.toString(p), res));
                values[index++] = (float) res;
            }
        }
        return values;
    }

    public static void test2d() {
        final int COUNT_SAMPLE = 100;
        final int COUNT = 1000;
        InterpolatingMicrosphere val = new InterpolatingMicrosphere(1, 50, .1, 0, .1,
                new UnitSphereRandomVectorGenerator(1));

        double maxVal = Math.PI * 6.;

        List<ImmutablePoint> pts = new ArrayList<>();

        for (int i = 0; i < COUNT_SAMPLE; i++) {
            double radians = 0. + (double) i / COUNT_SAMPLE * maxVal;
            if (ThreadLocalRandom.current().nextBoolean()) {
                pts.add(new ImmutablePoint(Math.toDegrees(radians), Math.toDegrees(radians), Math.sin(radians)));
            }
        }

        double[][] samplePoints = new double[pts.size()][1];
        double[] sampleVals = new double[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            samplePoints[i][0] = pts.get(i).getLattitude().doubleValue();
            sampleVals[i] = pts.get(i).getDepth().doubleValue();
        }


        logger.info("samplePoints={}, sampleVals={}", Arrays.deepToString(samplePoints), Arrays.toString(sampleVals));
        double[][] series = new double[2][COUNT];

        for (int i = 0; i < COUNT; i++) {
            double radians = 0. + (double) i / COUNT * maxVal;
            double res = val.value(new double[]{Math.toDegrees(radians)}, samplePoints, sampleVals, 1., 1.);
            series[0][i] = radians;
            series[1][i] = res;
//            System.out.println(String.format("%s\t%s", v, res));
        }

        ChartBuilder.xyChart(series, "showCorrelationChart", "i0|i1 instrument correlation", "i0/i1", "i0", "i1");

    }


}
