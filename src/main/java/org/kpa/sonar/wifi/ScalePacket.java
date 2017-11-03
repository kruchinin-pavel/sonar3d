package org.kpa.sonar.wifi;

public class ScalePacket extends BasePacket {
    public ScalePacket(byte[] data, PacketType type) {
        super(data, type);
    }

    public int getPacketNo() {
        return getByte(16);
    }

    @Override
    public String toString() {
        return "ScalePacket{" +
                "chrono=" + getChrono() +
                ", getViewDepth=" + getViewDepth() +
                ", getCurDepth=" + getCurDepth() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    public int getViewDepth() {
        return getIntByOffset(108);
    }

    public int getDepth() {
        return getIntByOffset(98);
    }

    public int getCurDepth() {
        return getIntByOffset(103);
    }

    public boolean isSonar() {
        return getByte(21) == 0;
    }

    public boolean isDownvision() {
        return getByte(21) == 1;
    }

}
