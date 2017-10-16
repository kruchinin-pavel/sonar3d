package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Surface;
import org.kpa.sonar.io.XmlTrackReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Modeller extends SimpleApplication {
    private static float[] heights = null;
    private static Surface coords;
    static int gridSize;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        loadTracks();
//        coords = generateSin(16);

        gridSize = coords.proposeMapSizeSquareMeters();
        heights = coords.buildHeights(gridSize);
        new Modeller().start();
    }

    public static Surface generateSin(int gridFacetSize) {
        return Surface.generateGrid(gridFacetSize, (x, z) -> -10 + Math.sin(x*z));
    }
    private static void loadTracks() throws ParserConfigurationException, SAXException, IOException {
        PointCollection collection;
        collection = new PointCollection();
        collection.addAll(XmlTrackReader.toList("src/test/resources/org/kpa/sonar/Tracks.gpx").get(1).getPoints(.5));
//        collection.fillBounds();
        coords = collection.getCoords();
    }


    @Override
    public void simpleInitApp() {
        Boat boat = Boat.createAndAttach(assetManager, rootNode);
        boat.getSpatial().setLocalTranslation(0, 0, 0);
        Bottom.createAndAttach(assetManager, rootNode, heights, gridSize + 1);
        OrtosJme.createAndAttach(assetManager, rootNode);
        if (coords != null) {
            coords.forEach(val -> PointJme.createAndAttach(val, assetManager, rootNode));
        }
        flyCam.setMoveSpeed(30);
    }
}
