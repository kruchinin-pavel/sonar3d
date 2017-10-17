package org.kpa.game;

public class Point3d implements Comparable<Point3d> {
    private final double x, y, z;

    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public int compareTo(Point3d o) {
        int ret = Double.compare(z, o.z);
        if (ret == 0) {
            ret = Double.compare(x, o.x);
        }
        return ret;
    }
}
