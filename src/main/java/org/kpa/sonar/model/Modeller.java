package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.sonar.Coords;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Track;
import org.kpa.sonar.io.XmlTrackReader;
import org.kpa.sonar.map.Interpolator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

public class Modeller extends SimpleApplication {


    private static float[] heights = null;

    private static PointCollection collection;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        collection = new PointCollection();
        Track track = XmlTrackReader.toList("src/test/resources/org/kpa/sonar/Tracks.gpx").get(1);
        collection.addAll(track.getPoints(.5));
        Coords coords = collection.getCoords();
        for (double[] pts : coords.getPts()) {
            System.out.println(String.format("x:y: %s", Arrays.toString(pts)));
        }
        collection.fillBounds();
        int gridSize = collection.proposeMapSizeSquareMeters();
        heights = Interpolator.buildHeights(gridSize, collection.getCoords());
//        heights = new float[gridSize * gridSize];
//        Arrays.fill(heights, -20);
        new Modeller().start();
    }

    @Override
    public void simpleInitApp() {
        Boat boat = Boat.createAndAttach(assetManager, rootNode);
        boat.getSpatial().setLocalTranslation(0, 0, 0);
        Bottom.createAndAttach(assetManager, rootNode, heights);
        collection.getPoints().forEach(val -> PointJme.createAndAttach(val, assetManager, rootNode));
        flyCam.setMoveSpeed(30);
    }
}
