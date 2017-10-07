package org.kpa.sonar.map;

import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;

import java.util.Arrays;

public class Interpolator {

    public static void main(String[] args) {
//        System.out.println(Arrays.toString(test3d(3, 10)));
        test3d(5, 5, true);
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
        final int DIMENSION = 1;
        InterpolatingMicrosphere val = new InterpolatingMicrosphere(DIMENSION, 5, .1, .1, .1,
                new UnitSphereRandomVectorGenerator(DIMENSION));

        double[][] samplePoints = new double[][]{{0.}, {5.}, {10.}};
        double[] sampleVals = new double[]{0., 5., 10.};
        for (int i = 0; i < 10; i++) {
            double v = 0. + i;
            double[] p = new double[]{v};
            double res = val.value(p, samplePoints, sampleVals, 1., 1.);
            System.out.println(String.format("%s\t%s", v, res));
        }
    }
}
