package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;
import org.kpa.sonar.PointCollection;
import org.kpa.sonar.Surface;
import org.kpa.sonar.interpolate.CachingInterpolator;
import org.kpa.sonar.interpolate.Data;
import org.kpa.sonar.interpolate.InterpolationCallable;
import org.kpa.sonar.io.XmlTrackReader;
import org.kpa.util.io.JsonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Modeller extends SimpleApplication {
    private static final Logger logger = LoggerFactory.getLogger(Modeller.class);
    private static Surface coords;

    public static void main(String[] args) throws Exception {
        coords = loadTracks(1);
        generateAndStoreTasks();

//        coords = generateSin(16);
        backgroundProcess();
        new Modeller().start();
//        KeyboardEnterActor.stopCurrentThreadOnEnter();
//        KeyboardEnterActor.await();
    }

    private static void backgroundProcess() throws Exception {
        CachingInterpolator interp = new CachingInterpolator(Paths.get("interp/tasks"));
        interp.start();
    }

    private static void generateAndStoreTasks() throws IOException {
        int index = 0;
        Data inputData = null;
        Data outputData = null;
        List<InterpolationCallable> interpolationCallables = coords.generateTaks();
        for (InterpolationCallable task : interpolationCallables) {
            if (inputData == null) {
                inputData = task.getInput();
                inputData.setFileName("interp/inputData.json");
                JsonDto.write("interp/inputData.json", inputData);
            }
            if (outputData == null) {
                outputData = task.getOutput();
                outputData.setFileName("interp/outputData.json");
                JsonDto.write("interp/outputData.json", outputData);
            }
            JsonDto.write("interp/tasks/task_" + System.currentTimeMillis() + "_" + index + ".json", task);
            index++;
            logger.info("Left to store {} tasks.", interpolationCallables.size() - index);
        }
    }

    public static Surface generateSin(int gridFacetSize) {
        return Surface.generateGrid(gridFacetSize, (x, z) -> -10 + Math.sin(x * z));
    }

    public static Surface loadTracks(int... id) throws ParserConfigurationException, SAXException, IOException {
        PointCollection col = XmlTrackReader
                .toCollection("src/test/resources/org/kpa/sonar/Tracks.gpx", id);
//        new OsmMap(col.getSwPoint(), col.getNePoint(), "cache")
//                .run().getNodeList().forEach(node -> col.add(new Point(node.getPosition().getLongitude(), node.getPosition().getLatitude(), 0))
//        );
        return col.getCoords().fillBounds();
    }


    @Override
    public void simpleInitApp() {
        Boat boat = Boat.createAndAttach(assetManager, rootNode);
        boat.getSpatial().setLocalTranslation(0, 0, 0);
        Bottom bottom = Bottom.createAndAttach(assetManager, rootNode, coords.proposeMapSizeSquareMeters());
        coords.buildHeights(bottom::setPoint);

        OrtosJme.createAndAttach(assetManager, rootNode);
        if (coords != null) {
            coords.forEach(val -> PointJme.createAndAttach(val, assetManager, rootNode));
        }
        flyCam.setMoveSpeed(Math.max((int) coords.getMaxDistance() / 30, 30));
    }
}
