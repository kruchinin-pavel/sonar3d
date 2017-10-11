package org.kpa.sonar.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bottom {
    private final Material mat_terrain;
    private final TerrainQuad spatial;
    private static final Logger logger = LoggerFactory.getLogger(Bottom.class);

    public Bottom(AssetManager assetManager, float[] mapArray) {

        /** 1. Create spatial material and load four textures into it. */
        mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");

        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));

        /** 1.2) Add GRASS texture into the red layer (Tex1). */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        /** 1.3) Add DIRT texture into the green layer (Tex2) */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        /** 1.4) Add ROAD texture into the blue layer (Tex3) */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 128f);


        int patchSize = 65;
        int totalSize = mapArray == null ? 513 : (int) Math.sqrt(mapArray.length) + 1;
        spatial = new TerrainQuad("my spatial", patchSize, totalSize, mapArray);

        /** 4. We give the spatial its material, position & scale it, and attach it. */
        spatial.setMaterial(mat_terrain);
        spatial.setLocalTranslation(0, 0, 0);
        spatial.setLocalScale(2f, 1f, 2f);
    }

    public TerrainQuad getSpatial() {
        return spatial;
    }

    public static Bottom createAndAttach(AssetManager assetManager, Node rootNode, float[] mapArray) {
        Bottom bottom = new Bottom(assetManager, mapArray);
        rootNode.attachChild(bottom.getSpatial());
        return bottom;
    }

}
