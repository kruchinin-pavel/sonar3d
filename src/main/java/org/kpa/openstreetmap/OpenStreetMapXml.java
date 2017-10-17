package org.kpa.openstreetmap;

import org.kpa.sonar.Track;
import org.kpa.util.xml.NLSIter;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;

import static org.kpa.util.xml.FileUtils.classOrPath;

public class OpenStreetMapXml {
    private final Node rootNode;

    public OpenStreetMapXml(NLSIter iter) {
        this.rootNode = iter.next("osm");
    }

    private Track build(Node node) {
        NLSIter iter = NLSIter.iter(node);
        Track track = new Track(iter.nextStr("name"));
        NLSIter seg = iter.nextIter("trkseg");
        while (seg.hasNext("trkpt")) {
            Node rpt = seg.next("trkpt");
            BigDecimal lon = new BigDecimal(rpt.getAttributes().getNamedItem("lon").getNodeValue());
            BigDecimal lat = new BigDecimal(rpt.getAttributes().getNamedItem("lat").getNodeValue());
            NLSIter ext = NLSIter.iter(rpt).nextIter("extensions");
            BigDecimal temp = ext.nextBD("WaterTemp");
            BigDecimal depth = ext.nextBD("WaterDepth");
            track.addPoint(lon, lat, depth, temp);
        }
        return track;
    }

    public static OpenStreetMapXml fromXmlFile(String xmlFileName) throws IOException, SAXException, ParserConfigurationException {
        return new OpenStreetMapXml(NLSIter.iter(classOrPath(xmlFileName)));
    }

}
