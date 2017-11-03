package org.kpa.sonar.wifi;

import com.google.common.primitives.Bytes;
import org.bouncycastle.util.encoders.Hex;

import java.util.function.BiFunction;

public enum PacketType {
    unknown(null, BasePacket::new),
    Sonar("01:01:27:00", SonarPacket::new),
    prefixMOSTCOMMONPACKET("01:01:27:00", BasePacket::new),
    Status("02:01:27:00", LocationPacket::new),
    prefixEMPTY_BIG_PACKET("03:01:27:00", BasePacket::new),
    prefixDEPTH("04:01:27:00", BasePacket::new),
    Temperature("08:01:27:00", CurrDepthPacket::new),
    Mark("06:01:27:00", MarkPacket::new),
    Heartbeat("0e:01:27:00", HeartbeatPacket::new),
    Type05("05:01:27:00", Type05Packet::new),
    Type07("07:01:27:00", Type07Packet::new),
    Type09("09:01:27:00", Type09Packet::new),
    Type0A("0a:01:27:00", Type0APacket::new),
    Type0D("0d:01:27:00", Type0DPacket::new),
    DownVision("0b:01:27:00", ScalePacket::new); // Need to clarify if this is a temperature. It reapeats almost every 1-2 seconds


    private final byte[] prefix;
    private final BiFunction<byte[], PacketType, ? extends BasePacket> factory;

    PacketType(String prefix, BiFunction<byte[], PacketType, ? extends BasePacket> factory) {
        if (prefix == null) this.prefix = new byte[0];
        else this.prefix = Hex.decode(prefix.replace(":", ""));
        this.factory = factory;
    }

    public static <T extends BasePacket> T getPacket(byte[] data) {
        for (PacketType val : values()) {
            if (val.prefix.length > 0 && Bytes.indexOf(data, val.prefix) == 0) {
                return (T) val.factory.apply(data, val);
            }
        }
        return (T) unknown.factory.apply(data, unknown);
    }
}
