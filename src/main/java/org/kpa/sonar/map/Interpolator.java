package org.kpa.sonar.map;

import org.kpa.sonar.PointCollection;
import org.kpa.sonar.io.XmlTrackReader;
import org.kpa.util.CHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Interpolator {
    private static final Logger logger = LoggerFactory.getLogger(Interpolator.class);


    public static void main(String[] args) {
        CHelper.startInWrap(args, Interpolator::_main);
    }

    private static void _main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        Interpolator interpolator = new Interpolator();
        interpolator.doInterpolate(args[0], args[1], Integer.parseInt(args[2]));
    }

    public void doInterpolate(String trackFileName, String exportFileName, int... trackId) throws IOException, SAXException, ParserConfigurationException {
        logger.info("Loading track {} from {} and put interpolation into {}", trackId, trackFileName, exportFileName);
        PointCollection collection = XmlTrackReader
                .toCollection("src/test/resources/org/kpa/sonar/Tracks.gpx", trackId);
        collection.getCoords().fillBounds().toGrid().storeToFile(exportFileName);
    }

//    public static float[] test3d(int points, double maxVal) {
//        return test3d(points, maxVal, false);
//    }
//
//    public static float[] test3d(int points, double maxVal, boolean print) {
//        double scale = maxVal / 10.;
//        double max = 10;
//        final int DIMENSION = 2;
//        InterpolatingMicrosphere val = new InterpolatingMicrosphere(DIMENSION, 50, 1, .1, -100,
//                new UnitSphereRandomVectorGenerator(DIMENSION));
//
//        double mid = max / 2;
//        double[][] samplePoints = new double[][]{
//                {0., 0.}, {0., mid}, {0., max},
//                {mid, 0.}, {mid, mid}, {mid, max},
//                {max, 0.}, {max, mid}, {max, max}
//        };
//        double[] sampleVals = new double[]{
//                0., mid, max,
//                0., mid, max,
//                0., mid, max,
//        };
//        float[] values = new float[points * points];
//        int index = 0;
//        for (int xi = 0; xi < points; xi++) {
//            for (int yi = 0; yi < points; yi++) {
//                double[] p = new double[]{(double) xi / points * max, (double) yi / points * max};
//                double res = val.value(p, samplePoints, sampleVals, 1., 1.) * scale;
//                if (print) System.out.println(String.format("%s\t%s", Arrays.toString(p), res));
//                values[index++] = (float) res;
//            }
//        }
//        return values;
//    }
//
//    public static void test2d() {
//        final int COUNT_SAMPLE = 100;
//        final int COUNT = 1000;
//        InterpolatingMicrosphere val = new InterpolatingMicrosphere(1, 50, .1, 0, .1,
//                new UnitSphereRandomVectorGenerator(1));
//
//        double maxVal = Math.PI * 6.;
//
//        List<ImmutablePoint> pts = new ArrayList<>();
//
//        for (int i = 0; i < COUNT_SAMPLE; i++) {
//            double radians = 0. + (double) i / COUNT_SAMPLE * maxVal;
//            if (ThreadLocalRandom.current().nextBoolean()) {
//                pts.add(new ImmutablePoint(Math.toDegrees(radians), Math.toDegrees(radians), Math.sin(radians)));
//            }
//        }
//
//        double[][] samplePoints = new double[pts.size()][1];
//        double[] sampleVals = new double[pts.size()];
//        for (int i = 0; i < pts.size(); i++) {
//            samplePoints[i][0] = pts.get(i).getLattitude().doubleValue();
//            sampleVals[i] = pts.get(i).getDepth().doubleValue();
//        }
//
//
//        logger.info("samplePoints={}, sampleVals={}", Arrays.deepToString(samplePoints), Arrays.toString(sampleVals));
//        double[][] series = new double[2][COUNT];
//
//        for (int i = 0; i < COUNT; i++) {
//            double radians = 0. + (double) i / COUNT * maxVal;
//            double res = val.value(new double[]{Math.toDegrees(radians)}, samplePoints, sampleVals, 1., 1.);
//            series[0][i] = radians;
//            series[1][i] = res;
////            System.out.println(String.format("%s\t%s", v, res));
//        }
//        ChartBuilder.xyChart(series, "showCorrelationChart", "i0|i1 instrument correlation", "i0/i1", "i0", "i1");
//    }
//

}
