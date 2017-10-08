package org.kpa.sonar.map;

import com.google.common.base.MoreObjects;
import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.kpa.sonar.TrackPoint;
import org.kpa.sonar.io.XmlTrackReader;

import java.util.Arrays;
import java.util.List;

public class Interpolator {

    public static void main(String[] args) {
//        System.out.println(Arrays.toString(test3d(3, 10)));
        test3d(5, 5, true);
    }

    public float[] read(int gridSize, String xmlTrackFile) {
        try {
            Double minX = null, maxX = null, minY = null, maxY = null, minZ = null, maxZ = null;
            List<TrackPoint> ptsList = XmlTrackReader.toPointList(xmlTrackFile);
            double[][] pts = new double[ptsList.size()][2];
            double[] vals = new double[ptsList.size()];
            for (int index = 0; index < ptsList.size(); index++) {
                double z = ptsList.get(index).getDepth().doubleValue();
                double x = ptsList.get(index).getLongitude().doubleValue();
                double y = ptsList.get(index).getLattitude().doubleValue();
                pts[index][0] = x;
                pts[index][1] = y;
                vals[index] = -z;
                minX = Math.min(MoreObjects.firstNonNull(minX, x), x);
                maxX = Math.max(MoreObjects.firstNonNull(maxX, x), x);
                minY = Math.min(MoreObjects.firstNonNull(minY, y), y);
                maxY = Math.max(MoreObjects.firstNonNull(maxY, y), y);
                minZ = Math.min(MoreObjects.firstNonNull(minZ, z), z);
                maxZ = Math.max(MoreObjects.firstNonNull(maxZ, z), z);
            }
            float values[] = new float[gridSize * gridSize];
            InterpolatingMicrosphere val = new InterpolatingMicrosphere(2, 50, 1, .1, -100,
                    new UnitSphereRandomVectorGenerator(2));
            int index = 0;
            for (int xi = 0; xi < gridSize; xi++) {
                for (int yi = 0; yi < gridSize; yi++) {
                    double[] p = new double[]{
                            minX + (maxX - minX) / (gridSize - 1) * xi,
                            minY + (maxY - minY) / (gridSize - 1) * yi
                    };
                    double res = val.value(p, pts, vals, 1., 1.);
                    System.out.println(String.format("%s\t%s", Arrays.toString(p), res));
                    values[index++] = (float) res;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
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
