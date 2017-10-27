package org.kpa.sonar.interpolate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.kpa.game.Point3d;
import org.kpa.sonar.Surface;
import org.kpa.util.io.JsonDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Data implements JsonWork {
    private double[][] pts;
    private double[] vals;
    private String fileName;

    @JsonIgnore
    public int gridSize() {
        return (int) (Math.sqrt(vals.length) - 1);
    }

    public Data(int gridSize) {
        pts = new double[(gridSize + 1)*(gridSize + 1)][2];
        vals = new double[(gridSize + 1)*(gridSize + 1)];
    }

    public Data(double[][] pts, double[] vals) {
        this.pts = pts;
        this.vals = vals;
    }

    public Data() {
    }

    public double[][] getPts() {
        return pts;
    }

    public void setPts(double[][] pts) {
        this.pts = pts;
    }

    public double[] getVals() {
        return vals;
    }

    public void setVals(double[] vals) {
        this.vals = vals;
    }

    private static Map<String, Data> map = new HashMap<>();

    public static Data getFromFile(String fileName) {
        return map.computeIfAbsent(fileName, _fileName -> {
            try {
                return JsonDto.read(_fileName, Data.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @JsonIgnore
    public String getFileName() {
        return fileName;
    }

    @JsonIgnore
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private boolean changed = false;

    @JsonIgnore
    public boolean isChanged() {
        return changed;
    }

    public void put(Point3d pt) {
        int index = Surface.toIndex(pt, gridSize());
        if (pt.getX() != pts[index][0]) {
            pts[index][0] = pt.getX();
            changed = true;
        }
        if (pts[index][1] != pt.getZ()) {
            changed = true;
            pts[index][1] = pt.getZ();
        }
        if (getVals()[index] != pt.getZ()) {
            getVals()[index] = pt.getZ();
            changed = true;
        }
    }
}
