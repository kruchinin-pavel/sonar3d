package org.kpa.sonar;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.kpa.game.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Surface {
    private final double[][] pts;
    private final double[] vals;
    private final double minX, minY, maxX, maxY;
    private static final Logger logger = LoggerFactory.getLogger(Surface.class);
    private final boolean isGrid;

    private boolean guessGrid() {
        double[] row = generateRow(proposeMapSizeSquareMeters());
        int index = 0;
        for (double z : row) {
            for (double x : row) {
                if (index >= pts.length || pts[index][0] != x || pts[index][1] != z) {
                    return false;
                }
                index++;
            }
        }
        return true;
    }

    public Surface(List<Point3d> _points) {
        List<Point3d> points = new ArrayList<>(_points);
        Collections.sort(points);
        pts = new double[points.size()][2];
        vals = new double[points.size()];
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
        isGrid = guessGrid();
    }

    public double getMaxDistance() {
        return Math.max(maxX - minX, maxY - minY);
    }

    public int proposeMapSizeSquareMeters() {
        double maxDistance = getMaxDistance();
        double n = Math.log(maxDistance) / Math.log(2) - 1;
        return (int) Math.pow(2, Math.ceil(n));
    }

    public double[][] getPts() {
        return pts;
    }

    public double[] getVals() {
        return vals;
    }


    public static double[] generateRow(int gridSize) {
        double[] row = new double[gridSize + 1];
        int index = 0;
        for (int v = -gridSize / 2; v <= gridSize / 2; v++) {
            row[index++] = v * 2;
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
        return isGrid;
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

    public Surface toGrid() {
        if (isGrid()) {
            return this;
        }
        float[] heigths = interpolateGrid();
        AtomicInteger index = new AtomicInteger();
        return generateGrid(proposeMapSizeSquareMeters(), (x, z) -> (double) heigths[index.getAndIncrement()]);
    }

    public float[] buildHeights() {
        return isGrid() ? getDirectGrid() : interpolateGrid();
    }

    private float[] interpolateGrid() {
        try {
            int gridSize = proposeMapSizeSquareMeters();
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
        Preconditions.checkArgument(((gridFacetSize & -gridFacetSize) == gridFacetSize), "Not a power of two: %s", gridFacetSize);
        List<Point3d> pts = new ArrayList<>();
        double[] doubles = generateRow(gridFacetSize);
        for (double z : doubles) {
            for (double x : doubles) {
                pts.add(new Point3d(x, yFunction.apply(x, z), z));
            }
        }
        return new Surface(pts);
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
        return new Surface(points);
    }

    private static final String[] header = new String[]{"X", "Y", "Z"};
    private static final CellProcessor[] processors = new CellProcessor[]{
            new Optional(new ParseDouble()),
            new Optional(new ParseDouble()),
            new Optional(new ParseDouble())
    };

    public void storeToFile(String fileName) throws IOException {
        try (CsvMapWriter csvMapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
            csvMapWriter.writeHeader(header);
            forEach(pt -> {
                try {
                    Map<String, Object> vals = new HashMap<>();
                    vals.put("X", pt.getX());
                    vals.put("Y", pt.getY());
                    vals.put("Z", pt.getZ());
                    csvMapWriter.write(vals, header, processors);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    public static Surface readFromFile(String fileName) throws IOException {
        List<Point3d> pts = new ArrayList<>();
        try (CsvMapReader reader = new CsvMapReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
            reader.getHeader(true);
            Map<String, Object> vals;
            while ((vals = reader.read(header, processors)) != null) {
                pts.add(new Point3d((double) vals.get("X"), (double) vals.get("Y"), (double) vals.get("Z")));
            }
        }
        return new Surface(pts);
    }
}
