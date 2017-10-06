package org.kpa.sonar;

import org.junit.Test;
import org.kpa.sonar.io.CsvPointsExport;
import org.kpa.sonar.io.XmlTrackReader;
import org.kpa.sonar.io.XmlWaypointReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class CsvPointsExportTest {

    public static final String FILE_NAME = "build/point.export.csv";

    @Test
    public void doExportTest() throws IOException {
        try (CsvPointsExport export = new CsvPointsExport(FILE_NAME)) {
            export.print(new Point(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.ZERO), "", 0);
            export.print(new Point(BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6), BigDecimal.ZERO), "", 0);
        }
        assertTrue(new File(FILE_NAME).exists());
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        try (CsvPointsExport export = new CsvPointsExport("export.csv")) {
            XmlTrackReader.fromXmlFile("src/test/resources/org/org.kpa/sonar/Tracks.xml")
                    .forEach(track -> track.getPoints().forEach(point -> {
                        export.print(point, "Track:" + track.getName(), point.getNum());
                    }));
            XmlWaypointReader.fromXmlFile("src/test/resources/org/org.kpa/sonar/Waypoints.xml")
                    .forEach(point -> export.print(point, "Wpt: " + point.getName(), 0));
        }
    }

}