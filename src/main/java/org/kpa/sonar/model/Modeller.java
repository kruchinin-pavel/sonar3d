package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.game.Point3d;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Surface;
import org.kpa.sonar.io.XmlTrackReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Modeller extends SimpleApplication {
    private static Surface coords;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        coords = loadTracks();
        coords = generateSin(16);
        new Modeller().start();
    }

    public static Surface generateSin(int gridFacetSize) {
//        List<Point3d> pts = new ArrayList<>();
//        for (int x = -gridFacetSize / 2; x <= gridFacetSize / 2; x++) {
//
//            for (int z = -gridFacetSize / 2; z <= gridFacetSize / 2; z++) {
//                pts.add(new Point3d(x, -10 + Math.sin(x * z), z));
//            }
//        }
//        return new Surface(pts, -1);
        return Surface.generateGrid(gridFacetSize, (x, z) -> -10 + Math.sin(x * z));
    }

    private static Surface loadTracks() throws ParserConfigurationException, SAXException, IOException {
        PointCollection collection;
        collection = new PointCollection();
        collection.addAll(XmlTrackReader.toList("src/test/resources/org/kpa/sonar/Tracks.gpx").get(0).getPoints(.5));
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
