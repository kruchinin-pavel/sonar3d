package org.kpa.sonar.wifi;


import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import org.apache.commons.codec.binary.Hex;
import org.kpa.sonar.PacketChrono;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BasePacket {
    private final byte[] data;
    private final PacketType type;
    private static byte[] bodyMark = new byte[]{(byte) 0x95, 0x5d, 0x1, (byte) 0xc9};
    private PacketChrono chrono;


    public BasePacket(byte[] data, PacketType type) {
        this.data = data;
        this.type = type;
        Preconditions.checkArgument(Bytes.indexOf(data, bodyMark) == 12, "Broken body packet: %s", this);
        Preconditions.checkArgument(data.length == getSize(), "Broken packet= buffer length is not equal to packetSize");
    }

    public int getSize() {
        return getInt(1, false);
    }


    public PacketChrono getChrono() {
        return chrono;
    }

    public void setChrono(PacketChrono chrono) {
        this.chrono = chrono;
    }

    public PacketType getType() {
        return type;
    }

    public String toHexStr() {
        return print(data, -1);
    }

    @Override
    public String toString() {
        return "BasePacket{" +
                "type=" + type +
                ", size=" + getSize() +
                ", data=" + toHexStr() +
                '}';
    }

    public byte[] getData() {
        return data;
    }

    private static final ThreadLocal<StringBuilder> bufRef = ThreadLocal.withInitial(StringBuilder::new);

    public static String print(byte[] data, int maxLength) {
        char[] chars;
        if (maxLength == -1) chars = Hex.encodeHex(data);
        else chars = Hex.encodeHex(Arrays.copyOf(data, Math.min(maxLength, data.length)));
        StringBuilder sb = bufRef.get();
        sb.setLength(0);
        for (int i = 0; i < chars.length; i++) {
            if (i % 2 == 0 && sb.length() > 0) sb.append(":");
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    protected int getInt(int no, boolean body) {
        return getIntByOffset(4 * no + (body ? 16 : 0));
    }

    protected int getIntByOffset(int offset) {
        return getBufAt(offset).getInt();
    }

    public ByteBuffer getBufAt(int offset) {
        return ByteBuffer.wrap(getData(), offset, Math.min(8, getData().length - offset)).order(java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public int getByte(int offset) {
        return Byte.toUnsignedInt(getData()[offset]);
    }

    public int lookupDouble(double min, double max) {
        for (int i = 0; i < data.length - 8; i++) {
            double val = getBufAt(i).getFloat();
            if (min < val && val < max) {
                return i;
            }
        }
        return -1;
    }

}
