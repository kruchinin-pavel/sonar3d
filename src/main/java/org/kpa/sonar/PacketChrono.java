package org.kpa.sonar;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class PacketChrono {
    private final long arrivalTime;
    private final long firstPacketArrivalTime;
    private final long packetNo;

    public PacketChrono(long arrivalTime, long firstPacketArrivalTime, long packetNo) {
        this.arrivalTime = arrivalTime;
        this.firstPacketArrivalTime = firstPacketArrivalTime;
        this.packetNo = packetNo;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getFirstPacketArrivalTime() {
        return firstPacketArrivalTime;
    }

    public double secondsSinceFirstPacket() {
        return (arrivalTime - firstPacketArrivalTime) * 1e-6;
    }

    public long getPacketNo() {
        return packetNo;
    }

    @Override
    public String toString() {
        return "PacketChrono{" +
                "packetNo=" + packetNo +
                ", sec.=" + Precision.round(secondsSinceFirstPacket(), 3) +
                '}';
    }
}
