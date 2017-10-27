package org.kpa.sonar.io;

import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Track;
import org.kpa.sonar.TrackPoint;
import org.kpa.util.xml.NLSIter;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.kpa.util.io.FileUtils.classOrPath;

public class XmlTrackReader implements Iterable<Track>, AutoCloseable {
    public static final String TRK = "trk";
    private final Node rootNode;

    public XmlTrackReader(NLSIter iter) {
        this.rootNode = iter.next("gpx");
    }

    @Override
    public Iterator<Track> iterator() {
        NLSIter iter = NLSIter.iter(rootNode);
        return new Iterator<Track>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext(TRK);
            }

            @Override
            public Track next() {
                Node wpt = iter.next(TRK);
                return build(wpt);
            }
        };
    }

    private static final DateTimeFormatter LDTF = DateTimeFormatter.ISO_DATE_TIME;//ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public static PointCollection toCollection(String xmlFileName, int... trackIndexes) throws ParserConfigurationException, SAXException, IOException {
        PointCollection collection;
        collection = new PointCollection();
        List<Track> tracks = XmlTrackReader.toList(xmlFileName);
        for (int index : trackIndexes) {
            collection.addAll(tracks.get(index).getPoints(.5));
        }
        return collection;
    }

    public static List<Track> toList(String xmlFileName) throws ParserConfigurationException, SAXException, IOException {
        return StreamSupport.stream(fromXmlFile(xmlFileName).spliterator(), false)
                .collect(Collectors.toList());
    }

    public static List<TrackPoint> toPointList(String xmlFileName) throws ParserConfigurationException, SAXException, IOException {
        List<TrackPoint> points = new ArrayList<>();
        toList(xmlFileName).forEach(track -> points.addAll(track.getPoints()));
        return points;
    }

    public static XmlTrackReader fromXmlFile(String xmlFileName) throws IOException, SAXException, ParserConfigurationException {
        return new XmlTrackReader(NLSIter.iter(classOrPath(xmlFileName)));
    }

    @Override
    public void close() {

    }
}
