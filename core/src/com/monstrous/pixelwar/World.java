package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    private Terrain terrain;
    private ModelAssets modelAssets;
    private Array<GameObject> gameObjects;
    private Vector3 tmpPosition = new Vector3();

    public World() {
        Gdx.app.debug("World", "constructor");

        GameObjectTypes types = new GameObjectTypes();  // instantiate 'static' class

        modelAssets = new ModelAssets();

        terrain = new Terrain();

        gameObjects = new Array<>();

        populate();
    }

    private void populate() {
        placeItem("Anti-Aircraft", -20, 0, 0);
        placeItem("Tank", -40, 0, 0);
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


    public void render(ModelBatch modelBatch, Environment environment, boolean mapView ) {
        terrain.render(modelBatch, environment);
        for(GameObject go : gameObjects ) {
            if(mapView && !go.type.showInMap)   // hide scenery in map view
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

//    public GameObject pickObject(Camera cam, int screenX, int screenY ) {
//        Ray ray = cam.getPickRay(screenX, screenY);
//        GameObject result = null;
//        float closestDistance2 = -1;
//
//        groundObject.intersect(ray, tmpPos);
//
//        hashGrid.getNearbyObjects(tmpPos, 50f, set);      // constant
//
//        for (GameObject go : set ) {
//            if(go.type.isGround)
//                continue;
////            if (go.army != Army.PLAYER)        // skip ground and markers, allow selection of buildings
////                continue;
//
//            go.getPosition(tmpPos);
//            float dist2 = ray.origin.dst2(tmpPos);
//            if (closestDistance2 >= 0f && dist2 > closestDistance2) continue;
//            if (Intersector.intersectRaySphere(ray, tmpPos, go.type.radius, null)) {
//                result = go;
//                closestDistance2 = dist2;
//            }
//        }
//        return result;
//    }
}
