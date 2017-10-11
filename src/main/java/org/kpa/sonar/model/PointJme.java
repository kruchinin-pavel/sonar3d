package org.kpa.sonar.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import org.kpa.sonar.IPoint;

public class PointJme {
    private final IPoint point;
    private Material material;
    private final Spatial spatial;

    public PointJme(IPoint point, AssetManager assetManager) {
        this.point = point;
        spatial = new Geometry("Box", new Sphere(10, 10, 0.1f));
        spatial.setMaterial(getMaterial(assetManager));
    }

    private Material getMaterial(AssetManager manager) {
        if (material == null) {
            material = new Material(manager, "Common/MatDefs/Misc/ShowNormals.j3md");
        }
        return material;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public static PointJme createAndAttach(IPoint point, AssetManager assetManager, Node rootNode) {
        PointJme pt = new PointJme(point, assetManager);
        pt.getSpatial().setLocalTranslation(
                point.getLongitudeMeters().floatValue(),
                -point.getDepth().floatValue(),
                point.getLattitudeMeters().floatValue());
        rootNode.attachChild(pt.getSpatial());
        return null;
    }

}
