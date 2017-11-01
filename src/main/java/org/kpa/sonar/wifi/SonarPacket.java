package org.kpa.sonar.wifi;

import java.awt.*;

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
                ", v0=" + getPacketSize() +
//                ", v1=" + getZoom1() +
//                ", v2=" + getZoom2() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    private int getPacketSize() {
        return getInt(1, false);
    }

    public int getPacketNo() {
        return getByte(33, false);
    }

    public int getZoom1() {
        return getInt(2, true);
    }

    public int getZoom2() {
        return getInt(3, true);
    }


    public boolean isSonar() {
        return getByte(20, true) == 1;
    }

    public boolean isDownVision() {
        return getByte(20, true) == 1;
    }
}
