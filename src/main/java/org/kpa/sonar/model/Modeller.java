package org.kpa.sonar.model;

import com.jme3.app.SimpleApplication;

public class Modeller extends SimpleApplication {
    public static void main(String[] args) {
        new Modeller().start();
    }

    @Override
    public void simpleInitApp() {
        Boat.createAndAttach(assetManager, rootNode).getSpatial().move(0,30,0);
        Bottom.createAndAttach(assetManager, rootNode);
        flyCam.setMoveSpeed(30);
    }
}
