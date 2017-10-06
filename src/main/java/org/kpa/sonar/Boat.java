package org.kpa.sonar;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

public class Boat {

    private final Spatial spatial;

    public Boat(AssetManager assetManager) {
        spatial = assetManager.loadModel("Models/boat/boat.obj");
        Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_default.setTexture("ColorMap", assetManager.loadTexture("Textures/boat/boat.tga"));
        spatial.setMaterial(mat_default);
        spatial.scale(.01f);
    }

    public Spatial getSpatial() {
        return spatial;
    }

}
