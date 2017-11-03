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
    private int lastDepth;

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
        if (lastDepth != packet.scale()) {
            System.out.println("new scale " + packet);
        }
        lastDepth = packet.scale();
    }


    public void addPacket(SonarPacket packet) {
        draw(packet, lastX++);
    }

    public void redraw() {
        g2.fillRect(xFrom, yFrom, xTo - xFrom + 1, yTo - yFrom + 1);
    }

    private void draw(SonarPacket packet, int column) {
        int y = yFrom;
        if (column >= xTo) return;

        SonarLine line = new SonarLine(packet.getPixels(), lastDepth);

        for (Color color : line.getPixels()) {
            g2.setColor(color);
            g2.fillRect(column, y++, 1, 1);
            if (y > yTo) {
                break;
            }
        }
    }

}
