package org.kpa.sonar.wifi;

public class CurrDepthPacket extends BasePacket {
    public CurrDepthPacket(byte[] data, PacketType type) {
        super(data, type);
    }

    @Override
    public String toString() {
        return "CurrDepthPacket{" +
                "isValid='" + isValid() + '\'' +
                ", val='" + getDepth() + '\'' +
                "toHexStr='" + toHexStr() + '\'' +
                '}';
    }

    public boolean isValid() {
        return getByte(16) == 2;
    }

    public int getDepth() {
        return isValid() ? getIntByOffset(17) : -1;
    }
}
