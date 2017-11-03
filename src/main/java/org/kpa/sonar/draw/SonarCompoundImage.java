package org.kpa.sonar.draw;

import org.kpa.sonar.wifi.ScalePacket;
import org.kpa.sonar.wifi.SonarPacket;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SonarCompoundImage {
    private final Graphics2D g2;
    private final BufferedImage off_image;
    private SonarImage sonarImage;
    private SonarImage downVisionImage;

    public SonarCompoundImage() {
        int width = 3300;
        int height = 2000;
        off_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = off_image.createGraphics();
        g2.setBackground(Color.black);
        sonarImage = new SonarImage(g2, 0, width, 0, 999);
        downVisionImage = new SonarImage(g2, 0, width, 1000, 2000);
    }

    public void addPacket(SonarPacket packet) {
        if (packet.isSonar()) {
            sonarImage.addPacket(packet);
        } else {
            downVisionImage.addPacket(packet);
        }
    }

    public void addPacket(ScalePacket packet) {
        if (packet.isSonar()) {
            sonarImage.addPacket(packet);
        } else {
            downVisionImage.addPacket(packet);
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
