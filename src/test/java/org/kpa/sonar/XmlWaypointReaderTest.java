package org.kpa.sonar;

import org.junit.Assert;
import org.junit.Test;
import org.kpa.sonar.io.XmlWaypointReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;

public class XmlWaypointReaderTest {

    @Test
    public void testLoad() throws IOException, SAXException, ParserConfigurationException {
        XmlWaypointReader reader = XmlWaypointReader.fromXmlFile("org/kpa/sonar/Waypoints.xml");
        Iterator<Waypoint> iterator = reader.iterator();
        Waypoint obj = iterator.next();
        Assert.assertNotNull(obj);

        Waypoint obj2 = iterator.next();
        Assert.assertNotNull(obj);

    }

}