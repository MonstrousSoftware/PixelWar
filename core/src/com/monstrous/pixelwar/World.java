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

    public static String PLAYER = "Blue";
    public static String ENEMY = "Red";

    public static Terrain terrain;
    private ModelCache cache;
    private static ModelAssets modelAssets;
    private static Array<GameObject> gameObjects;
    private static Array<GameObject> deleteList;
    private static Vector3 tmpPosition = new Vector3();
    private static Vector3 tmpVelocity = new Vector3();
    private static Army playerArmy;
    private GameObject playerFlag;
    private GameObject enemyFlag;

    public World() {
        Gdx.app.debug("World", "constructor");

        Armies armies = new Armies();
        playerArmy = Armies.getPlayerArmy();
        modelAssets = new ModelAssets();
        GameObjectTypes types = new GameObjectTypes();  // instantiate 'static' class

        terrain = new Terrain();

        gameObjects = new Array<>();
        deleteList = new Array<>();


        buildCache();
        populate();
    }

    // place all scenery in a model cache
    private void buildCache() {
        cache = new ModelCache();
        placeRandom("Tree1", 300);
        placeRandom("Stone1", 200);
        placeRandom("Stone2", 200);
        placeRandom("Stone3", 200);

        cache.begin();
        cache.add(terrain.modelInstance);
        for(GameObject go : gameObjects ) {
            cache.add(go.modelInstance);
        }
        cache.end();
        gameObjects.clear();
    }

    private void populate() {
        placeItem(PLAYER, "Tank", -40, 0, 0);
        placeItem(PLAYER, "Tank", -60, 0, 0);
        placeItem(PLAYER, "Tank", 10, 0, 0);
        playerFlag = placeItem(PLAYER, "Flag", -12, 0, 0);
        placeItem(PLAYER, "AirShip", 0, 30, 0);
        placeItem(PLAYER, "Tower", 0, 40, 0);
        placeItem(PLAYER, "Anti-Aircraft", 0, 0, 0);
        placeItem(PLAYER, "Anti-Aircraft", 10, 10, 90);

        placeItem(ENEMY, "Anti-Aircraft", 50, 0, 0);
        placeItem(ENEMY, "Anti-Aircraft", 50, 50, 0);
        placeItem(ENEMY, "Anti-Aircraft", 50, -50, 0);
        placeItem(ENEMY, "Tank", 100, 30, 0);
        placeItem(ENEMY, "Tank", 60, 20, 0);
        placeItem(ENEMY, "Tank", 100, 10, 0);
        enemyFlag = placeItem(ENEMY, "Flag", 100, 0, 0);
        placeItem(ENEMY, "AirShip", 50, 30, 0);
        placeItem(ENEMY, "Tower", 50, 40, 0);
    }

    private void placeRandom(String name, int count){
        for(int n = 0; n < count; n++) {
            float xx = (float) (Math.random()-0.5f)*Settings.worldSize;
            float zz = (float) (Math.random()-0.5f)*Settings.worldSize;
            float r = (float) (Math.random()*360f);
            placeItem("Neutral", name, xx, zz, r);
        }
    }

    public static GameObject placeItem(String armyName, String name, float x, float z, float angle){

        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        tmpVelocity.set(0,0,0);
        GameObject go = new GameObject(armyName, name, tmpPosition, angle, tmpVelocity);
        gameObjects.add(go);
        return go;
    }

    public static GameObject spawnItem(String armyName, String name, Vector3 position, float angle, Vector3 velocity){
        GameObject go = new GameObject(armyName, name, position, angle, velocity);
        gameObjects.add(go);
        return go;
    }


    public static GameObject testForCollision(GameObject subject) {
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go == subject)
                continue;
            if(go.army == subject.army)
                continue;
            float dist2 = subject.position.dst2(go.position);
            if(dist2 < go.type.radius * go.type.radius)
                return go;
        }
        return null;
    }

    public GameObject selectRandomUnit() {
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army != playerFlag.army || !go.type.isMobile || go.type.isProjectile)
                continue;
            return go;
        }
        return null;
    }

    public static GameObject closestEnemy(GameObject subject, float radius) {
        GameObject closest = null;
        float minDist = Float.MAX_VALUE;
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army.isNeutral || go.army == subject.army || go.type.isProjectile)
                continue;
            float dist2 = subject.position.dst2(go.position);
            if(dist2 > radius*radius)
                continue;
            if (dist2 < minDist) {
                minDist = dist2;
                closest = go;
            }
        }
        return closest;
    }

    public static GameObject closestEnemyAirship(GameObject subject, float radius) {
        GameObject closest = null;
        float minDist = Float.MAX_VALUE;
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army.isNeutral || go.army == subject.army || go.type.isProjectile || !go.type.isAirship)
                continue;
            float dist2 = subject.position.dst2(go.position);
            if(dist2 > radius*radius)
                continue;
            if (dist2 < minDist) {
                minDist = dist2;
                closest = go;
            }
        }
        return closest;
    }



    public void update( float deltaTime ){
        deleteList.clear();
        for(GameObject go : gameObjects ) {
            go.update(deltaTime);
            if(go.toRemove)
                deleteList.add(go);
        }
        gameObjects.removeAll(deleteList, true);
    }

    public boolean gameOver() {
        if(playerFlag.isDying || enemyFlag.isDying)
            return true;
        return false;
    }

    public boolean haveWon() {
        if( enemyFlag.isDying)
            return true;
        return false;
    }


    public void render(ModelBatch modelBatch, Environment environment, boolean mapView ) {

        modelBatch.render(cache, environment);

       // terrain.render(modelBatch, environment);
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
        cache.dispose();
    }

    private Vector3 tmpPos = new Vector3();

    public GameObject pickObject(Camera cam, int screenX, int screenY ) {
        Ray ray = cam.getPickRay(screenX, screenY);
        GameObject result = null;
        float closestDistance = 9999999f;

        for (GameObject go : gameObjects) {
            if(go.isDying)
                continue;

            if(go.army != playerArmy)   // can only select own units, not enemy units or neutral ones
                continue;

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
            placeItem("Neutral", "Arrow", location.x, location.z, 0);
        return hit;
    }

}
