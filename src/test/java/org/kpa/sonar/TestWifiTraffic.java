package org.kpa.sonar;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.junit.Test;
import org.kpa.sonar.wifi.BasePacket;
import org.kpa.sonar.wifi.PacketType;
import org.kpa.sonar.wifi.SonarPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestWifiTraffic {
    private static final Logger logger = LoggerFactory.getLogger(TestWifiTraffic.class);
    private static final Joiner joiner = Joiner.on(":");

    public String firstPacketType = "01:01:27:00:ba:02:00:00:74:00";
    List<String> packets = new ArrayList<>();
    Multimap<Integer, String> packetClasses = TreeMultimap.create();
    Set<String> prefixes = new TreeSet<>();

    @Test
    public void doTestTraffic() throws IOException {
        final Pcap pcap = Pcap.openStream("src/test/resources/org/kpa/sonar/raymarine.2017.10.28.dmp");
        AtomicInteger counter = new AtomicInteger();
        AtomicLong firstArrivalTime = new AtomicLong();
        AtomicLong packetCounter = new AtomicLong();
        pcap.loop(packet -> {
            if (firstArrivalTime.get() == 0) {
                firstArrivalTime.set(packet.getArrivalTime());
            }
            packetCounter.incrementAndGet();
            if (!packet.hasProtocol(Protocol.UDP)) {
                return true;
            }
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
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
            BasePacket rPacket = PacketType.getPacket(buffer.getArray());
            if (!(rPacket instanceof SonarPacket)) return true;
            rPacket.setChrono(new PacketChrono(packet.getArrivalTime(), firstArrivalTime.get(), packetCounter.get()));
            String hexStr = rPacket.toHexStr();
            packetClasses.put(rPacket.getSize(), hexStr);
            logger.info("Packet {} ", rPacket);
//            if (!hexStr.startsWith(firstPacketType)) return true;
//            logger.info("UDP ({} bytes at {}): {}", data.length, packet.getArrivalTime(), hexStr);
            packets.add(rPacket.getChrono().getPacketNo() + ":" + rPacket.getChrono().secondsSinceFirstPacket() + ":" + hexStr + ":LLL");
//            return counter.incrementAndGet() < 10;
            return true;
        });
//        Collections.sort(packets);


        Files.write(Paths.get("out.txt"), packets);
        packetClasses.keySet().forEach(key -> {
            List<String> values = new ArrayList<>(packetClasses.get(key));
            Collections.sort(values);
            String value = values.get(0);
            String prefix = findCommonStart(values);
            System.out.println("Length: " + key + ". count=" + values.size() + ". Common prefix: " + prefix);
            if (prefix != null && values.size() > 1) prefixes.add(prefix);
        });

        prefixes.forEach(System.out::println);
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
    }

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
