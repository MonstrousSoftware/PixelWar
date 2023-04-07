package com.monstrous.pixelwar;


import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.utils.*;

// rocks and trees
// rendering into a model cache

public class Scenery implements Disposable {

    private World world;
    private ModelCache cache;
    private Array<GameObject> gameObjects;

    public Scenery(World world ) {
        this.world = world;
        buildCache();
    }

    // place all scenery in a model cache
    private void buildCache() {
        gameObjects = new Array<>();
        cache = new ModelCache();
        placeRandom("Tree1", 300);
        placeRandom("Stone1", 200);
        placeRandom("Stone2", 200);
        placeRandom("Stone3", 200);

        cache.begin();
        for(GameObject go : gameObjects ) {
            cache.add(go.modelInstance);
        }
        cache.end();
        gameObjects.clear();
    }

    private void placeRandom(String name, int count){
        for(int n = 0; n < count; n++) {
            float xx = (float) (Math.random()-0.5f)*(Settings.worldSize-5f);    // don't plant trees to close to the edge
            float zz = (float) (Math.random()-0.5f)*(Settings.worldSize-5f);
            float r = (float) (Math.random()*360f);
            world.placeItem("Neutral", name, xx, zz, r);
        }
    }

    public void render(ModelBatch modelBatch, Environment environment ) {
        modelBatch.render(cache, environment);  // scenery items
    }

    @Override
    public void dispose() {
        cache.dispose();
    }
}
