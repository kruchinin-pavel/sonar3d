package org.kpa.sonar.io;

import org.kpa.sonar.Waypoint;
import org.kpa.util.io.FileUtils;
import org.kpa.util.xml.NLSIter;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class XmlWaypointReader implements Iterable<Waypoint> {
    public static final String WPT = "wpt";
    private final Node rootNode;

    public XmlWaypointReader(NLSIter iter) {
        this.rootNode = iter.next("gpx");
    }

    @Override
    public Iterator<Waypoint> iterator() {
        NLSIter iter = NLSIter.iter(rootNode);
        return new Iterator<Waypoint>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext(WPT);
            }

            @Override
            public Waypoint next() {
                Node wpt = iter.next(WPT);
                return build(wpt);
            }
        };
    }

    private Waypoint build(Node node) {
        NLSIter iter = NLSIter.iter(node);
        NLSIter ext = iter.nextIter("extensions");
        return new Waypoint(iter.nextStr("name"),
                iter.nextLdt("time"),
                new BigDecimal(node.getAttributes().getNamedItem("lon").getNodeValue()),
                new BigDecimal(node.getAttributes().getNamedItem("lat").getNodeValue()),
                ext.nextBD("WaterDepth"),
                ext.nextBD("WaterTemp"));
    }

    public static XmlWaypointReader fromXmlFile(String xmlFileName) throws IOException, SAXException, ParserConfigurationException {
        return new XmlWaypointReader(NLSIter.iter(FileUtils.classOrPath(xmlFileName)));
    }

    public static List<Waypoint> toList(String xmlFileName) throws ParserConfigurationException, SAXException, IOException {
        return StreamSupport.stream(fromXmlFile(xmlFileName).spliterator(), false)
                .collect(Collectors.toList());
    }

}
