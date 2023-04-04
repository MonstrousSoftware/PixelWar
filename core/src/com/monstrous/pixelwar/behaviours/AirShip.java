package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.ModelAssets;
import com.monstrous.pixelwar.Sounds;
import com.monstrous.pixelwar.World;

public class AirShip extends Behaviour {

    public static final float TOWER_RADIUS = 5f;

    private boolean hasBomb;

    public AirShip(GameObject go) {

        super(go);
        hasBomb = true;
    }

    @Override
    public void update( World world, float deltaTime ) {

        go.targetAngle = go.angle;  // make bomb follow airship orientation

        if(hasBomb) {
            GameObject target = world.closestEnemy(go, Bomb.BOMB_RADIUS);
            if (target != null) {
                // drop the bomb
                // - hide modelInstance2 and spawn a dropping bomb
                hasBomb = false;
                go.modelInstance2 = null;

                GameObject bomb = world.spawnItem(go.army.name, "Bomb", go.position, go.targetAngle, go.velocity);
                Sounds.playSound(Sounds.FALLING_BOMB);
            }
        }
        else {
            GameObject tower = world.closestTower(go, TOWER_RADIUS);    // are we close to a tower?
            if(tower != null) {
                // reload a bomb
                Model model = ModelAssets.getModel("Assets");
                go.modelInstance2 = new ModelInstance(model, go.type.modelName2);
                go.modelInstance2.transform.rotate(Vector3.Y, go.angle).trn(go.position);
                hasBomb = true;
            }
        }

        // death animation
        if (go.isDying) {
            go.modelInstance.transform.setFromEulerAngles(go.angle, 20f, 15f).trn(go.position); // tilt angle
            go.isMovingToDestination = false;
            go.velocity.y = -0.8f;  // move down
         }
        if(go.position.y < -3f) // underground
            go.toRemove = true;
    }
}
