package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.sonar.Surface;
import org.kpa.sonar.map.Interpolator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Modeller extends SimpleApplication {
    private static float[] heights = null;
    private static Surface coords;
    static int gridSize;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
//        PointCollection collection;
//        collection = new PointCollection();
//        collection.addAll(XmlTrackReader.toList("src/test/resources/org/kpa/sonar/Tracks.gpx").get(1).getPoints(.5));
//        collection.fillBounds();
//        Surface coords = collection.getCoords();

        coords = Interpolator.generateSin(16);
        gridSize = coords.proposeMapSizeSquareMeters();
        heights = coords.buildHeights(gridSize);

//        gridSize = 4;
//        heights = new float[(gridSize + 1) * (gridSize + 1)];
//        Arrays.fill(heights, -20);
        new Modeller().start();
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
