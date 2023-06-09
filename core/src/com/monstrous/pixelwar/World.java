package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    public final static String PLAYER = "Blue";
    public final static String ENEMY = "Red";

    public  Terrain terrain;

    private ModelAssets modelAssets;
    public  Array<GameObject> gameObjects;
    private  Array<GameObject> deleteList;
    private  Vector3 tmpPosition = new Vector3();
    private  Vector3 tmpVelocity = new Vector3();
    private  Army playerArmy;
    private GameObjectTypes types;
    private GameObject playerFlag;
    private GameObject enemyFlag;
    private AI ai;
    private float previousFlagHealth;
    private  ParticleEffects particleEffects;
    private  boolean shaking;
    private Armies armies;
    private ModelInstance xyzModelInstance;
    private ShapeRenderer shapeRenderer;
    private int[] typeCounts;
    private Scenery scenery;


    public World( Camera cam ) {
        Gdx.app.debug("World", "constructor");

        armies = new Armies();
        playerArmy = armies.getPlayerArmy();
        modelAssets = new ModelAssets();
        types = new GameObjectTypes();

        shapeRenderer = new ShapeRenderer();

        gameObjects = new Array<>();
        deleteList = new Array<>();

        Gdx.app.debug("World", "make terrain");
        terrain = new Terrain();
        scenery = new Scenery(this);

        particleEffects = new ParticleEffects(cam);

        populate();
        xyzModelInstance = makeArrows();

        previousFlagHealth = playerFlag.healthPoints;
        ai = new AI(enemyFlag, gameObjects);


        shaking = false;
        typeCounts = new int[5];    // 5 types

    }



    private ModelInstance makeArrows() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createXYZCoordinates(20f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked);
        return new ModelInstance(model, new Vector3(0,10,0));
    }

    private void populate() {
        playerFlag = placeItem(PLAYER, "Flag", 0, -100, 0);


        placeItem(PLAYER, "Anti-Aircraft", 20, -80, 90);
        placeItem(PLAYER, "Anti-Aircraft", -20, -80, 90);

        placeItem(PLAYER, "Tank", -40, -70, 90); // 90
        placeItem(PLAYER, "Tank", -30, -70, 90);
        placeItem(PLAYER, "Tank", 40, -70, 90);
        placeItem(PLAYER, "Tank", 30, -70, 90);

        placeItem(PLAYER, "AirShip", 0, -60, 0);
        placeItem(PLAYER, "AirShip", 50, -60, 0);
        placeItem(PLAYER, "Tower", 10, -60, 0);


        enemyFlag = placeItem(ENEMY, "Flag", 0, 100, -90);
        placeItem(ENEMY, "Anti-Aircraft", 20, 80, 90);
        placeItem(ENEMY, "Anti-Aircraft", -20, 80, 90);

        placeItem(ENEMY, "Tank", -40, 70, -90);
        placeItem(ENEMY, "Tank", -30, 70, -90);
        placeItem(ENEMY, "Tank", 40, 70, -90);
        placeItem(ENEMY, "Tank", 30, 70, -90);

        placeItem(ENEMY, "AirShip", 0, 60, 0);
        placeItem(ENEMY, "AirShip", -80, 60, 0);
        placeItem(ENEMY, "Tower", 10, 60, 0);
    }



    public  GameObject placeItem(String armyName, String name, float x, float z, float angle){

        Army army = armies.findArmy(armyName);
        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        tmpVelocity.set(0,0,0);
        return spawnItem(army, name, tmpPosition, angle, tmpVelocity);
    }

    public  GameObject spawnItem(Army army, String typeName, Vector3 position, float angle, Vector3 velocity){
        GameObjectType type = types.findType(typeName);
        GameObject go = new GameObject(terrain, army, type, position, angle, velocity);
        go.behaviour = types.getTypeBehaviour(go);
        gameObjects.add(go);
        return go;
    }

    public  void spawnFire(float x, float z) {
        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        particleEffects.addFire(tmpPosition);
    }

    public  void spawnExplosion(Vector3 position) {

        particleEffects.addExplosion(position);
    }

    public  void spawnSmokeTrail(Matrix4 transform) {

        particleEffects.addExhaustFumes(transform);
    }

    public  GameObject testForCollision(GameObject bullet) {

        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army == bullet.army)  // prevent friendly fire
                continue;
            if(go.type.isProjectile)        // don't collide bullets with bullets
                continue;
            // add bounding box centre to object position (especially important for airship)
            tmpPosition.set(bullet.position).sub(go.position);
            if( go.type.bbox.contains(tmpPosition) )
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
        // fall-back: select structure
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army != playerFlag.army || go.type.isProjectile)
                continue;
            return go;
        }
        return null;
    }


    public GameObject selectNextUnitOfType(GameObjectType type, GameObject currentObject) {
        // get next object of same type
        boolean foundCurrent = false;
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(!foundCurrent && go != currentObject)
                continue;
            foundCurrent = true;
            if( go == currentObject)
                continue;
            if(go.army != playerFlag.army || go.type != type)
                continue;
            return go;
        }
        // start from the top if nothing found after current object
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army != playerFlag.army || go.type != type)
                continue;
            return go;
        }
        return null;
    }

    public GameObject selectNextUnit(String typeName, GameObject currentObject) {
        GameObjectType type = types.findType(typeName);
        if(type == currentObject.type) {
            return selectNextUnitOfType(type, currentObject);
        }

        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army != playerFlag.army || go.type != type)
                continue;
            return go;
        }
        return null;
    }

    public GameObject closestEnemy(GameObject subject, float radius) {
        GameObject closest = null;
        float minDist = Float.MAX_VALUE;
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army.isNeutral || go.army == subject.army || go.type.isProjectile || go.type.isAirship || go.isDying)
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

    // damage all enemies in blast circle
    public void blastEffect(GameObject bomb, float radius) {

        Sounds.playSound(Sounds.EXPLOSION);

        //actual bomb position (centre of bbox)
        bomb.type.bbox.getCenter(tmpPosition);
        tmpPosition.add(bomb.position);

        // damage all enemies close to the bomb (not airships though)
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if(go.army.isNeutral || go.army == bomb.army || go.type.isProjectile || go.isDying || go.type.isAirship)
                continue;
            float dist2 = tmpPosition.dst2(go.position);
            if(dist2 > radius*radius)
                continue;
            go.takeDamage(this, 150);
        }
        spawnExplosion(tmpPosition);
        shaking = true;
    }

    public boolean isShaking() {
        if(shaking) {
            shaking = false;
            return true;
        }
        return false;
    }

    public GameObject closestTower(GameObject subject, float radius) {
        GameObject closest = null;
        float minDist = Float.MAX_VALUE;
        for(int i = 0; i < gameObjects.size; i++ ) {
            GameObject go = gameObjects.get(i);
            if( go.army != subject.army || !go.type.isTower || go.isDying)  // friendly tower, not dying
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

    public GameObject closestEnemyAirship(GameObject subject, float radius) {
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
        for(int i = 0; i < 5; i++)
            typeCounts[i] = 0;
        deleteList.clear();
        for(GameObject go : gameObjects ) {
            go.update(this, deltaTime);
            if(go.toRemove)
                deleteList.add(go);
            else if(go.army == playerArmy && go.type.typeId >= 0 && !go.isDying ) {
                typeCounts[go.type.typeId]++;
            }
        }
        gameObjects.removeAll(deleteList, true);


        ai.update(deltaTime);
        particleEffects.update(deltaTime);
    }

    public int getTypeCount(int typeId ) {
        return typeCounts[typeId];
    }


    public boolean isFlagUnderAttack() {
        if(playerFlag.healthPoints == previousFlagHealth )
            return false;
        previousFlagHealth = playerFlag.healthPoints;
        return true;
    }

    public boolean gameOver() {
        return (playerFlag.isDying || enemyFlag.isDying);

    }

    public boolean haveWon() {
        return ( enemyFlag.isDying);
    }


    private Vector3 start = new Vector3();
    private Vector3 end = new Vector3();

    public void render(ModelBatch modelBatch, Environment environment) {

        modelBatch.render(terrain.modelInstance, environment);
        scenery.render(modelBatch, environment);

        for(GameObject go : gameObjects ) {

            modelBatch.render(go.modelInstance, environment);
            if(go.modelInstance2 != null)
                modelBatch.render(go.modelInstance2, environment);
        }
        particleEffects.render(modelBatch);

        //modelBatch.render(xyzModelInstance, environment);
    }


    public void renderNormals(Camera cam) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.setProjectionMatrix(cam.combined);

        for(float x = - 20; x < 20; x += 3f) {
            for( float z = -20; z < 20; z += 3f ) {
                float y = terrain.getHeight(x, z);
                start.set(x, y, z);
                terrain.getNormal(x, z, end);
                end.scl(3).add(start);
                shapeRenderer.line(start.x, start.y, start.z, end.x, end.y, end.z);
                shapeRenderer.line(start.x, start.y, start.z, end.x, start.y, start.z);
                shapeRenderer.line(start.x, start.y, start.z, start.x, start.y, end.z);
            }
        }

        //for(float x = - 100; x < 100; x += 25f) {
        float x = -10f;
        for( float z = -50; z < 50; z += 1f ) {
            float y = terrain.getHeight(x, z);
            start.set(x, y, z);
            terrain.getNormal(x, z, end);
            end.scl(5).add(start);
            shapeRenderer.line(start.x, start.y, start.z, end.x, end.y, end.z);
            shapeRenderer.line(start.x, start.y, start.z, end.x, start.y, start.z);
            shapeRenderer.line(start.x, start.y, start.z, start.x, start.y, end.z);
            }
        //}

        shapeRenderer.setColor(Color.RED);
        for(GameObject go : gameObjects ) {
            if(!go.type.followsTerrain)
                continue;

            start.set(go.position);
            end.set(go.terrainNormal).scl(10f).add(go.position);

            shapeRenderer.line(start.x, start.y, start.z, end.x, end.y, end.z);
        }
        shapeRenderer.end();
}

    @Override
    public void dispose() {
        Gdx.app.debug("World", "dispose");
        modelAssets.dispose();
        terrain.dispose();
        scenery.dispose();
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
        if(hit) {
            float ht = terrain.getHeight(location.x, location.z);
            Gdx.app.log("terrain height", "ht: "+ht);
            placeItem("Neutral", "Arrow", location.x, location.z, 0);
        }
        return hit;
    }

}
