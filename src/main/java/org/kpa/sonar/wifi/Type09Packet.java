package org.kpa.sonar.wifi;

/**
 * Packet streamed every 10 seonds.
 * Common content:
 * { 0e:01:27:00:55:00:00:00:74:00:00:00:95:5d:01:c9:09:42:6c:07:00:46:69:72:65:66:6c:79:5f:46:69:66:6f:44
 * :61:74:61:5f:30:5f:30:2e:72:65:63:20:30:6b:42:20:0a:4e:6f:72:6d:61:6c
 * :00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00}
 */
public class Type09Packet extends BasePacket {
    public Type09Packet(byte[] data, PacketType type) {
        super(data, type);
    }

    @Override
    public String toString() {
        return "Type09Packet{" +
                "toHexStr='" + toHexStr() + '\'' +
                '}';
    }
}
