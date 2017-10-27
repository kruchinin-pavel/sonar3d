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
import java.util.concurrent.TimeUnit;
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

    private static int align(double point, int gridSize) {
        int ipt = (int) point / 2;
        int i = gridSize / 2 + ipt;
        Preconditions.checkArgument(i >= 0 && i <= gridSize, "Index is outside of range: gridSize=%s, value=%s", gridSize, point);
        return i;
    }

    public static int toIndex(Point3d pt, int gridSize) {
        return align(pt.getZ(), gridSize) * (gridSize + 1) + align(pt.getX(), gridSize);
    }


    public void getDirectGrid(Consumer<Point3d> consumer) {
        for (int index = 0; index < this.vals.length; index++) {
            consumer.accept(new Point3d(pts[index][0], pts[index][1], this.vals[index]));
        }
        consumer.accept(null);
    }

    public boolean isGrid() {
        return isGrid;
    }

    private static class InterpY implements Runnable {
        private final InterpolatingMicrosphere sphere =
                new InterpolatingMicrosphere(2, 50, 1, 0, -0,
                        new UnitSphereRandomVectorGenerator(2));
        private final float zVal;
        private final float xVal;
        private final Surface coords;
        private final Consumer<Point3d> consumer;

        public InterpY(float zVal, float xVal, Surface coords, Consumer<Point3d> consumer) {
            this.zVal = zVal;
            this.xVal = xVal;
            this.coords = coords;
            this.consumer = consumer;
        }

        public void run() {
            double[] p = new double[]{xVal, zVal};
            double res = sphere.value(p, coords.getPts(), coords.getVals(), 1., 1.);
            consumer.accept(new Point3d(xVal, (float) res, zVal));
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

    public float[] interpolateGrid() {
        float[] pts = getFlatMap();
        int gridSize = proposeMapSizeSquareMeters();
        CountDownLatch cdl = new CountDownLatch(1);
        interpolateGrid(pt -> {
            if (pt == null) {
                cdl.countDown();
            } else {
                pts[toIndex(pt, gridSize)] = (float) pt.getY();
            }
        });
        try {
            cdl.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return pts;
    }


    public void buildHeights(Consumer<Point3d> consumer) {
        if (isGrid()) {
            getDirectGrid(consumer);
        } else {
            interpolateGrid(consumer);
        }
    }


    public float[] getFlatMap() {
        int gridSize = proposeMapSizeSquareMeters();
        return new float[(gridSize + 1) * (gridSize + 1)];
    }

    private void interpolateGrid(Consumer<Point3d> consumer) {
        try {
            int gridSize = proposeMapSizeSquareMeters();
            double[] row = generateRow(gridSize);
            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (double xVal : row) {
                for (double zVal : row) {
                    service.execute(() -> new InterpY((float) zVal, (float) xVal, this, consumer::accept).run());
                }
            }
            service.shutdown();
            new Thread(() -> {
                try {
                    service.awaitTermination(10, TimeUnit.MINUTES);
                    consumer.accept(null);
                } catch (InterruptedException e) {
                    logger.warn("Interrupted.");
                    Thread.currentThread().interrupt();
                }
            }).start();
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
