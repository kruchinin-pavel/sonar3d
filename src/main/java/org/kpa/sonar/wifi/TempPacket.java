package org.kpa.sonar.wifi;

public class TempPacket extends BasePacket {
    public TempPacket(byte[] data, PacketType type) {
        super(data, type);
    }

    @Override
    public String toString() {
        return "TempPacket{" +
                "toHexStr='" + toHexStr() + '\'' +
                '}';
    }
}
