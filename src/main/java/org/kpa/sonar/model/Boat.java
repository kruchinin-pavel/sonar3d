package org.kpa.sonar.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Boat {

    private final Spatial spatial;

    public Boat(AssetManager assetManager) {
        spatial = assetManager.loadModel("Models/boat/boat.obj");
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Textures/boat/boat.tga"));
        spatial.setMaterial(mat_default);
//        spatial.scale(.06f);
        spatial.setLocalTranslation(0f, 0f, 0f);

    }

    public Spatial getSpatial() {
        return spatial;
    }


    public static Boat createAndAttach(AssetManager assetManager, Node rootNode) {
        Boat boat = new Boat(assetManager);
        rootNode.attachChild(boat.getSpatial());
        double hadVol = ((com.jme3.bounding.BoundingBox) boat.getSpatial().getWorldBound()).getVolume();
        float scale = (float) Math.cbrt(4f / hadVol);
        boat.getSpatial().scale(scale);
        double hasVol = ((com.jme3.bounding.BoundingBox) boat.getSpatial().getWorldBound()).getVolume();
        return boat;
    }

}
