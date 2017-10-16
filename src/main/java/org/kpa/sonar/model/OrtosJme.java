package org.kpa.sonar.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

public class OrtosJme {

    private final Spatial xAxis;
    private final Spatial yAxis;
    private final Spatial zAxis;
    private final Node node;

    public OrtosJme(AssetManager assetManager) {
        xAxis = new Geometry("x-axis", new Line(new Vector3f(0, 0, 0), new Vector3f(10, 0, 0)));
        xAxis.setMaterial(getMaterial(assetManager, ColorRGBA.Green));
        yAxis = new Geometry("y-axis", new Line(new Vector3f(0, 0, 0), new Vector3f(0, 10, 0)));
        yAxis.setMaterial(getMaterial(assetManager, ColorRGBA.White));
        zAxis = new Geometry("z-axis", new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 10)));
        zAxis.setMaterial(getMaterial(assetManager, ColorRGBA.Cyan));
        node = new Node();
        node.attachChild(xAxis);
        node.attachChild(yAxis);
        node.attachChild(zAxis);
//        zAxis.setLocalTranslation(0,0,0);
//        yAxis.setLocalTranslation(0,0,0);
//        yAxis.setLocalTranslation(0,0,0);
    }

    private Material getMaterial(AssetManager manager, ColorRGBA color) {
        Material material = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        return material;
    }

    public Spatial getxAxis() {
        return xAxis;
    }

    public static OrtosJme createAndAttach(AssetManager assetManager, Node rootNode) {
        OrtosJme pt = new OrtosJme(assetManager);
        pt.node.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(pt.node);
        return null;
    }

}
