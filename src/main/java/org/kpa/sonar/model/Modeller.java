package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.osm.OpenStreetMapRest;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Surface;
import org.kpa.sonar.io.XmlTrackReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Modeller extends SimpleApplication {
    private static final Logger logger = LoggerFactory.getLogger(Modeller.class);
    private static Surface coords;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        coords = loadTracks(2);
//        coords = generateSin(16);
        new Modeller().start();
    }

    public static Surface generateSin(int gridFacetSize) {
        return Surface.generateGrid(gridFacetSize, (x, z) -> -10 + Math.sin(x * z));
    }

    public static Surface loadTracks(int... id) throws ParserConfigurationException, SAXException, IOException {
        PointCollection collection = XmlTrackReader
                .toCollection("src/test/resources/org/kpa/sonar/Tracks.gpx", id);
        logger.info("URL to OpenStreetMap: {}", new OpenStreetMapRest(collection, bounds).getBoundingBoxRequest());
        return collection.getCoords().fillBounds();
    }


    @Override
    public void simpleInitApp() {
        Boat boat = Boat.createAndAttach(assetManager, rootNode);
        boat.getSpatial().setLocalTranslation(0, 0, 0);
//        Bottom.createAndAttach(assetManager, rootNode, coords.buildHeights());
        OrtosJme.createAndAttach(assetManager, rootNode);
        if (coords != null) {
            coords.forEach(val -> PointJme.createAndAttach(val, assetManager, rootNode));
        }
        flyCam.setMoveSpeed(30);
    }
}
