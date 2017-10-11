package org.kpa.sonar;

import java.util.List;

public class Coords {
    private final double[][] pts;
    private final double[] vals;

    public Coords(List<IPoint> points, double tenzor) {
        pts = new double[points.size()][2];
        vals = new double[points.size()];
        int index = 0;
        for (IPoint point : points) {
            if (point.getDepth() == null) continue;
            double z = point.getDepth().doubleValue() * tenzor;
            double x = point.getLongitudeMeters().doubleValue() * tenzor;
            double y = point.getLattitudeMeters().doubleValue() * tenzor;
            pts[index][0] = x;
            pts[index][1] = y;
            vals[index] = -z;
            index++;
        }
    }

    public double[][] getPts() {
        return pts;
    }

    public double[] getVals() {
        return vals;
    }
}
