package org.kpa.sonar;

import com.google.common.base.Joiner;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.junit.Test;
import org.kpa.sonar.draw.SonarCompoundImage;
import org.kpa.sonar.wifi.BasePacket;
import org.kpa.sonar.wifi.PacketType;
import org.kpa.sonar.wifi.ScalePacket;
import org.kpa.sonar.wifi.SonarPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TestWifiTraffic {
    private static final Logger logger = LoggerFactory.getLogger(TestWifiTraffic.class);
    private static final Joiner joiner = Joiner.on(":");
    public static final long PRINT_MARGIN_PACKETS_COUNT = 100L;

    List<String> packets = new ArrayList<>();
    SonarCompoundImage image = new SonarCompoundImage();
    Iterator<Long> points = Arrays.asList(new Long[]{13962L, 15324L, 15980L, 23257L, 23865L, 25425L, 26378L, 26880L}).iterator();

    @Test
    public void doTestTraffic() throws IOException {
        final Pcap pcap = Pcap.openStream("src/test/resources/org/kpa/sonar/raymarine.2017.10.28.dmp");
        AtomicLong firstArrivalTime = new AtomicLong();
        AtomicLong packetCounter = new AtomicLong();
        AtomicReference<SonarPacket> lastSonarPacketRef = new AtomicReference<>();
        AtomicInteger maxPaketSize = new AtomicInteger();
        LinkedHashSet<Long> pointsOfChange = new LinkedHashSet<>();
        AtomicLong currPoint = new AtomicLong(points.next());
        pcap.loop(pCapPacket -> {
            if (firstArrivalTime.get() == 0) {
                firstArrivalTime.set(pCapPacket.getArrivalTime());
            }
            packetCounter.incrementAndGet();
            if (!pCapPacket.hasProtocol(Protocol.UDP)) {
                return true;
            }
            UDPPacket udpPacket = (UDPPacket) pCapPacket.getPacket(Protocol.UDP);
            if (udpPacket.getDestinationPort() != 3201) {
                return true;
            }
            if (!udpPacket.getSourceIP().equals("192.168.0.1")) {
                return true;
            }
            Buffer buffer = udpPacket.getPayload();
            if (buffer == null) {
                return true;
            }

            BasePacket packet = PacketType.getPacket(buffer.getArray());
            packet.setChrono(new PacketChrono(pCapPacket.getArrivalTime(), firstArrivalTime.get(), packetCounter.get()));
            if (packet instanceof SonarPacket) {
                SonarPacket sonarPacket = (SonarPacket) packet;
                image.addPacket(sonarPacket);
                if (lastSonarPacketRef.get() != null) {
                    SonarPacket lastSonarPacker = lastSonarPacketRef.get();
                    if (lastSonarPacker.getPxDepth() != sonarPacket.getPxDepth()) {
                        pointsOfChange.add(sonarPacket.getChrono().getNo());
                    }
                }
                lastSonarPacketRef.set(sonarPacket);

            } else if (packet instanceof ScalePacket) {
//                logger.info("Got scale pCapPacket: {}", packet);
                image.addPacket((ScalePacket) packet);
            } else {
                if (currPoint.get() + PRINT_MARGIN_PACKETS_COUNT < packet.getChrono().getNo()) {
                    if (points.hasNext()) {
                        currPoint.set(points.next());
                    }
                    return true;
                }
                if (currPoint.get() - PRINT_MARGIN_PACKETS_COUNT > packet.getChrono().getNo()) {
                    return true;
                }
            }
            String hexStr = packet.toHexStr();

            maxPaketSize.set(Math.max(maxPaketSize.get(), packet.getSize()));

            packets.add(packet.getChrono().getNo() + ":" + hexStr + ":LLL");
//            logger.info("Packet {}", packet);
            return true;
        });

        StringBuilder sb = new StringBuilder("no");
        for (int i = 0; i < maxPaketSize.get(); i++) sb.append(":").append(i);
        packets.add(0, sb.toString());

        image.store("sonar_image.png");
        Files.write(Paths.get("out.txt"), packets);
//        logger.info("Points of changing zoom: {}", pointsOfChange);
    }

    public static String findCommonStart(List<String> packets) {
        int maxLength = packets.get(0).length();
        int max = maxLength;
        int min = 0;
        int currLength = max;
        while (max - min > 1) {
            if (isEqual(packets, currLength)) {
                min = currLength;
            } else {
                max = currLength;
            }
            currLength = (max + min) / 2;
        }
        return isEqual(packets, currLength) ? packets.get(0).substring(0, currLength) : null;
    }import org.kpa.sonar.draw.SonarImage;


    private static boolean isEqual(List<String> packets, int length) {
        String prefix = null;
        for (String str : packets) {
            if (prefix == null) {
                prefix = str.substring(0, length);
            }
            if (!str.startsWith(prefix)) return false;
        }
        return true;
    }
}
