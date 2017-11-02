package org.kpa.sonar.wifi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SonarImage {

    public static final int WIDTH = 3300;
    private int height = -1;
    private Graphics2D g2;
    private BufferedImage off_image;
    private int sonarColumn = 0;
    private int downVisionColumn = 0;

    public SonarImage() {
        height = 2000;
        off_image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        g2 = off_image.createGraphics();
        g2.setBackground(Color.black);
    }

    public void addPacket(SonarPacket packet) {
        if (packet.isSonar()) {
            draw(packet, 0, 1000, sonarColumn++);
        } else {
            draw(packet, 1000, 2000, downVisionColumn++);
        }
    }

    private void draw(SonarPacket packet, int y, int maxHeight, int column) {
        if (column >= WIDTH) return;
        for (Color color : packet.getPixels()) {
            g2.setColor(color);
            g2.fillRect(column, y++, 1, 1);
            if (y > maxHeight) {
                break;
            }

        }
    }

    public void store(String fileName) {
        try {
            ImageIO.write(off_image, "png", new java.io.File(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
