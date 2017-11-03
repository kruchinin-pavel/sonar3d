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
        return "StatusPacket{" +
                "#=" + getPacketNo() +
                ", getDepth=" + getDepth() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    public int getDepth() {
        return getIntByOffset(108);
    }

    public boolean isSonar() {
        return getByte(21) == 0;
    }

    public boolean isDownvision() {
        return getByte(21) == 1;
    }

}
