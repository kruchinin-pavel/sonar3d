package org.kpa.sonar.wifi;

import com.google.common.base.Preconditions;

public class LocationPacket extends BasePacket {
    public LocationPacket(byte[] data, PacketType type) {
        super(data, type);
    }

    public int getPacketNumber() {
        Preconditions.checkArgument(getData().length > 20, "Packet is too short: %s", this);
        return getData()[20];
    }

    @Override
    public String toString() {
        return "LocationPacket{" +
                "#=" + getPacketNumber() +
                ", toHexStr='" + toHexStr() + '\'' +
                '}';
    }
}
