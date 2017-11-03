package org.kpa.sonar.draw;

import com.google.common.base.Preconditions;
import org.kpa.sonar.wifi.ScalePacket;
import org.kpa.sonar.wifi.SonarPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SonarImage {
    private final Graphics2D g2;
    private List<SonarLine> pixels = new ArrayList<>();
    private final int yFrom;
    private final int yTo;
    private final int xFrom;
    private final int xTo;
    private int lastX;
    private int viewDepth = -1;
    private int lastDepth = -1;

    public SonarImage(Graphics2D g2, int xFrom, int xTo, int yFrom, int yTo) {
        Preconditions.checkArgument(yFrom < yTo);
        Preconditions.checkArgument(xFrom < xTo);
        this.g2 = g2;
        this.yFrom = yFrom;
        this.yTo = yTo;
        this.xFrom = xFrom;
        this.xTo = xTo;
        lastX = xFrom;
    }

    public void addPacket(ScalePacket packet) {
        if (viewDepth == -1) {
            viewDepth = packet.getViewDepth();
        }
        lastDepth = packet.getViewDepth();
        if (viewDepth < lastDepth) {
            viewDepth = lastDepth;
            redraw();
        }
    }


    public void addPacket(SonarPacket packet) {
        SonarLine line = new SonarLine(packet.getPixels(), lastDepth);
        pixels.add(line);
        draw(line, lastX++);
    }

    public void redraw() {
        g2.setColor(Color.black);
        g2.fillRect(xFrom, yFrom, xTo - xFrom + 1, imageHeight());
        lastX = xFrom;
        for (SonarLine line : pixels) {
            draw(line, lastX++);
        }
    }

    private void draw(SonarLine line, int column) {
        if (column >= xTo) return;
        int heightPixels = (int) (imageHeight() * ((double) line.getDepth()) / viewDepth);
        int pixelCount = line.getPixels().size();
        int pixelIndex = 0;
        int yDrawn = 0;
        for (Color color : line.getPixels()) {
            g2.setColor(color);
            int y = (int) ((double) pixelIndex / pixelCount * heightPixels);
            if (yDrawn < y) {
                g2.fillRect(column, yFrom + yDrawn, 1, y - yDrawn + 1);
                yDrawn = y;
            }
            pixelIndex++;
            Preconditions.checkArgument(y <= yTo);
        }
    }

    private int imageHeight() {
        return this.yTo - yFrom + 1;
    }

}
