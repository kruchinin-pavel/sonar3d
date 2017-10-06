package org.kpa.sonar.model;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.*;
import com.jme3.texture.Texture;
import org.apache.commons.lang3.ArrayUtils;
import org.kpa.sonar.map.Interpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bottom {
    private final Material mat_terrain;
    private final TerrainQuad spatial;
    private final Logger logger = LoggerFactory.getLogger(Bottom.class);

    public Bottom(AssetManager assetManager) {

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

        /** 2. Create the height map */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
                "Scenes/mountains512.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();
        float[] mapArray = heightmap.getHeightMap();

        /** 3. We have prepared material and heightmap.
         * Now we create the actual spatial:
         * 3.1) Create a TerrainQuad and name it "my spatial".
         * 3.2) A good value for spatial tiles is 64x64 -- so we supply 64+1=65.
         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
         * 3.5) We supply the prepared heightmap itself.
         */

        float[] heightData = Interpolator.test3d(512, 255);
        mapArray = heightData;

        List<Float> vals = Arrays.asList(ArrayUtils.toObject(mapArray));
        logger.info("Min/Max: {}/{}", Collections.min(vals), Collections.max(vals));


        int patchSize = 65;
        spatial = new TerrainQuad("my spatial", patchSize, 513, mapArray);

        /** 4. We give the spatial its material, position & scale it, and attach it. */
        spatial.setMaterial(mat_terrain);
        spatial.setLocalTranslation(0, -100, 0);
        spatial.setLocalScale(2f, 1f, 2f);
    }

    public TerrainQuad getSpatial() {
        return spatial;
    }

    public static Bottom createAndAttach(AssetManager assetManager, Node rootNode) {
        Bottom bottom = new Bottom(assetManager);
        rootNode.attachChild(bottom.getSpatial());
        return bottom;
    }

}
