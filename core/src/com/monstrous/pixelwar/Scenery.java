package com.monstrous.pixelwar;


import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.utils.*;

// rocks and trees
// rendering into a model cache

public class Scenery implements Disposable {

    private ModelCache cache;

    public Scenery(World world ) {

        // place all scenery in a model cache
        // we can delete all the items as game objects once we have the cache constructed.
        //
        cache = new ModelCache();
        placeRandom(world, "Tree1", 300);
        placeRandom(world, "Stone1", 200);
        placeRandom(world, "Stone2", 200);
        placeRandom(world, "Stone3", 200);

        cache.begin();
        for(GameObject go : world.gameObjects ) {
            cache.add(go.modelInstance);
        }
        cache.end();
        world.gameObjects.clear();
    }

    private void placeRandom(World world, String name, int count){
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
