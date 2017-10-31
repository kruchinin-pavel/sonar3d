package org.kpa.sonar;

import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestWifiTraffic {
    private static final Logger logger = LoggerFactory.getLogger(TestWifiTraffic.class);

    @Test
    public void doTestTraffic() throws IOException {
        final Pcap pcap = Pcap.openStream("src/test/resources/org/kpa/sonar/raymarine.2017.10.28.dmp");
        AtomicInteger counter = new AtomicInteger();
        pcap.loop(packet -> {

            if (packet.hasProtocol(Protocol.TCP)) {

                TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                Buffer buffer = tcpPacket.getPayload();
                if (buffer != null) {
                    logger.info("TCP: {} bytes" + buffer.getArray().length);
                }
            } else if (packet.hasProtocol(Protocol.UDP)) {

                UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                Buffer buffer = udpPacket.getPayload();
                if (buffer != null) {
                    logger.info("UDP: {} bytes" + buffer.getArray().length);
                }
            }
            return counter.incrementAndGet() < 10;
        });
    }
}
