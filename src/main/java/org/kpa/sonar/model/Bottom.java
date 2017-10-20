package org.kpa.sonar.model;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Bottom {
    private final Material mat_terrain;
    private final TerrainQuad spatial;
    private static final Logger logger = LoggerFactory.getLogger(Bottom.class);

    private final float[] mapArray;

    public Texture generateAlphaMap(AssetManager manager) {
        int faceLength = (int) Math.sqrt(mapArray.length);
        BufferedImage off_Image = new BufferedImage(faceLength, faceLength, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = off_Image.createGraphics();
        g2.setBackground(Color.black);
        for (int z = 1; z < faceLength; z++) {
            for (int x = 1; x < faceLength; x++) {
                float h0 = mapArray[(z - 1) * faceLength + x - 1];
                float h1 = mapArray[(z) * faceLength + x];
                if (h0 >= 0 && h1 >= 0) {
                    g2.setColor(Color.RED);
                } else {
                    g2.setColor(Color.GREEN);
                }
                g2.drawRect(x - 1, z - 1, 1, 1);
            }
        }
        try {
//            ImageIO.write(off_Image, "png", new java.io.File("saved.png"));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(off_Image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return manager.loadAssetFromStream(new TextureKey("./saved.png"), is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Bottom(AssetManager assetManager, float[] mapArray) {
        this.mapArray = mapArray;


        /** 1. Create spatial material and load four textures into it. */
        mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");

//        assetManager.registerLocator("ram",ContentTextureLocator.class);
        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
//        mat_terrain.setTexture("Alpha", assetManager.loadTexture("ram/saved.png"));
        mat_terrain.setTexture("Alpha", generateAlphaMap(assetManager));

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

        spatial = new TerrainQuad("my spatial", Math.max((int) Math.sqrt(mapArray.length) / 4, 2), (int) Math.sqrt(mapArray.length), mapArray);

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
