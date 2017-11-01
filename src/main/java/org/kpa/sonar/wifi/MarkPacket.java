package org.kpa.sonar.wifi;

public class MarkPacket extends BasePacket {
    public MarkPacket(byte[] data, PacketType type) {
        super(data, type);
    }

    @Override
    public String toString() {
        return "MarkPacket{" +
                "toHexStr='" + toHexStr() + '\'' +
                '}';
    }
}
