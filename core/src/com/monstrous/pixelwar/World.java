package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    public Terrain terrain;
    private ModelAssets modelAssets;
    private Array<GameObject> gameObjects;
    private Vector3 tmpPosition = new Vector3();

    public World() {
        Gdx.app.debug("World", "constructor");

        modelAssets = new ModelAssets();
        GameObjectTypes types = new GameObjectTypes();  // instantiate 'static' class

        terrain = new Terrain();

        gameObjects = new Array<>();

        populate();
    }

    private void populate() {
        placeItem("Anti-Aircraft", -20, 0, 0);
        placeItem("Tank", -40, 0, 0);
        placeItem("Tank", -60, 0, 0);
        placeItem("Tank", -80, 0, 0);
        placeItem("Flag", 0, 0, 0);


//        placeRandom("Tree1", 2600);
//        placeRandom("Stone1", 200);
//        placeRandom("Stone2", 200);
//        placeRandom("Stone3", 200);

        placeItem("AirShip", 0, 30, 0);
    }

    private void placeRandom(String name, int count){
        for(int n = 0; n < count; n++) {
            float xx = (float) (Math.random()-0.5f)*Settings.worldSize;
            float zz = (float) (Math.random()-0.5f)*Settings.worldSize;
            float r = (float) (Math.random()*360f);
            placeItem(name, xx, zz, r);
        }
    }

    private void placeItem(String name, float x, float z, float angle){

        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        GameObject go = new GameObject(name, tmpPosition, angle);
        gameObjects.add(go);
    }


    public void update( float deltaTime ){
        for(GameObject go : gameObjects ) {
            go.update(deltaTime);
        }
    }

    public void render(ModelBatch modelBatch, Environment environment, boolean mapView ) {
        terrain.render(modelBatch, environment);
        for(GameObject go : gameObjects ) {
            if(mapView && go.type.isScenery)   // hide scenery in map view
                continue;

            modelBatch.render(go.modelInstance, environment);
            if(go.modelInstance2 != null)
                modelBatch.render(go.modelInstance2, environment);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.debug("World", "dispose");
        modelAssets.dispose();
        terrain.dispose();
    }

    private Vector3 tmpPos = new Vector3();

    public GameObject pickObject(Camera cam, int screenX, int screenY ) {
        Ray ray = cam.getPickRay(screenX, screenY);
        GameObject result = null;
        float closestDistance = 9999999f;

        for (GameObject go : gameObjects) {

            go.type.bbox.getCenter(tmpPos); // center of volume relative to object origin
            tmpPos.add(go.position);

            float dist = ray.origin.dst2(tmpPos);  // distance between ray and object
            if (dist > closestDistance) // skip, we've seen a better candidate already
                continue;

            if (Intersector.intersectRaySphere(ray, tmpPos, go.type.radius, null)) {
                result = go;
                closestDistance = dist;
            }
        }
        if (result != null) {
            Gdx.app.log("pickObject", "dist: "+closestDistance);

            return result;
        }
        return null;
    }

    public boolean pickLocation(Camera cam, int screenX, int screenY, Vector3 location ) {
        Ray ray = cam.getPickRay(screenX, screenY);

        // find where screen ray hits the terrain
        boolean hit = terrain.intersect(ray, location);
        Gdx.app.log("terrain", "pos: "+location+" hit: "+hit);
        if(hit)
            placeItem("Flag", location.x, location.z, 0);
        return hit;
    }

}
