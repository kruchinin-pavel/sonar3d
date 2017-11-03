package org.kpa.sonar.draw;

import java.awt.*;
import java.util.List;

public class SonarLine {
    private final List<Color> pixels;
    private final int depth;

    public SonarLine(List<Color> pixels, int depth) {
        this.pixels = pixels;
        this.depth = depth;
    }

    public List<Color> getPixels() {
        return pixels;
    }

    public int getDepth() {
        return depth;
    }
}
