package org.kpa.sonar;

import org.apache.commons.math3.util.Precision;

public class PacketChrono {
    private final long arrivalTime;
    private final long firstPacketArrivalTime;
    private final long no;

    public PacketChrono(long arrivalTime, long firstPacketArrivalTime, long no) {
        this.arrivalTime = arrivalTime;
        this.firstPacketArrivalTime = firstPacketArrivalTime;
        this.no = no;
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

    public long getNo() {
        return no;
    }

    @Override
    public String toString() {
        return "PacketChrono{" +
                "packetNo=" + no +
                ", sec.=" + Precision.round(secondsSinceFirstPacket(), 3) +
                '}';
    }
}
