package org.kpa.sonar;

import org.junit.Test;
import org.kpa.sonar.io.XmlTrackReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertNotNull;

public class XmlTrackReaderTest {
    @Test
    public void doLoadTest() throws ParserConfigurationException, SAXException, IOException {
        XmlTrackReader reader = XmlTrackReader.fromXmlFile("org/kpa/sonar/Tracks.gpx");

        Iterator<Track> iter = reader.iterator();
        Track obj1 = iter.next();
        Track obj2 = iter.next();
        assertNotNull(obj1);
        assertNotNull(obj2);
        System.out.println("Obj1=" + obj1);
        System.out.println("Obj2=" + obj2);
    }

}