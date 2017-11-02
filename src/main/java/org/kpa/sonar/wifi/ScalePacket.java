package org.kpa.sonar.wifi;

import com.google.common.base.Preconditions;

public class ScalePacket extends BasePacket {
    public ScalePacket(byte[] data, PacketType type) {
        super(data, type);
    }

    public int getPacketNo() {
        return getByte(16);
    }

    @Override
    public String toString() {
        return "StatusPacket{" +
                "#=" + getPacketNo() +
                ", scale=" + scale() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    public int scale() {
        return getIntByOffset(108);
    }

    public boolean isSonar() {
        return getByte(21) == 0;
    }

    public boolean isDownvision() {
        return getByte(21) == 1;
    }

}
