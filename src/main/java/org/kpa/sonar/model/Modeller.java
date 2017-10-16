package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Surface;
import org.kpa.sonar.io.XmlTrackReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Modeller extends SimpleApplication {
    private static Surface coords;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        coords = loadTracks();
        coords = generateSin(16);
        new Modeller().start();
    }

    public static Surface generateSin(int gridFacetSize) {
        return Surface.generateGrid(gridFacetSize, (x, z) -> -10 + Math.sin(x * z));
    }

    private static Surface loadTracks() throws ParserConfigurationException, SAXException, IOException {
        PointCollection collection;
        collection = new PointCollection();
        collection.addAll(XmlTrackReader.toList("src/test/resources/org/kpa/sonar/Tracks.gpx").get(1).getPoints(.5));
        return collection.getCoords().fillBounds();
    }


    @Override
    public void simpleInitApp() {
        Boat boat = Boat.createAndAttach(assetManager, rootNode);
        boat.getSpatial().setLocalTranslation(0, 0, 0);
        Bottom.createAndAttach(assetManager, rootNode, coords.buildHeights());
        OrtosJme.createAndAttach(assetManager, rootNode);
        if (coords != null) {
            coords.forEach(val -> PointJme.createAndAttach(val, assetManager, rootNode));
        }
        flyCam.setMoveSpeed(30);
    }
}
