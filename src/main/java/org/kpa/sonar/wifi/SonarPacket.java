package org.kpa.sonar.wifi;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;

import java.awt.*;
import java.util.ArrayList;

public class SonarPacket extends BasePacket {
    public SonarPacket(byte[] data, PacketType type) {
        super(data, type);
    }

    //    Color color = new Color(0x41,0xb4,0xe6);
//    Color color = new Color(0xfa, 0xfd, 0xf1);
//    Color color = new Color(0x22, 0xb5, 0xf8);
    @Override
    public String toString() {
        return "SonarPacket{" +
                "#=" + getPacketNo() +
                ", chrono=" + getChrono() +
                ", pxDepth=" + getPxDepth() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    public int getPacketNo() {
        return getByte(33, false);
    }

    private int pxDepth = -1;

    /**
     * Depth in bytes
     *
     * @return
     */
    public int getPxDepth() {
        if (pxDepth == -1) {
            pxDepth = getInt(3, true);
        }
        return pxDepth;
    }


    public boolean isSonar() {
        return getByte(20, true) == 0;
    }

    public boolean isDownvision() {
        return getByte(20, true) == 1;
    }

    public int getPxOffset() {
        return getSize() - getPxDepth() + 1;
    }


    public java.util.List<Color> getPixels() {
        ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), 0, 255, new org.jzy3d.colors.Color(1, 1, 1, .5f));
        java.util.List<Color> pixels = new ArrayList<>();
        for (int y = getPxOffset(); y < getData().length; ) {
//            Color e = new Color(0f, 0f, 1f, (float) (getByte(y++, false) / 255.));
            org.jzy3d.colors.Color jzyColor = mapper.getColor(getByte(y++, false));
            pixels.add(new Color(jzyColor.r, jzyColor.g, jzyColor.b));
        }
        return pixels;
    }

}
