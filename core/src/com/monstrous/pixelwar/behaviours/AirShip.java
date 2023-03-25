package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.Gdx;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.World;

public class AirShip extends Behaviour {

    public AirShip(GameObject go) {
        super(go);
    }

    @Override
    public void update( float deltaTime ) {

        // death animation
        if (go.isDying) {
            go.modelInstance.transform.setFromEulerAngles(go.angle, 20f, 15f).trn(go.position);
            go.isMovingToDestination = false;
            go.velocity.y = -0.8f;
         }
        if(go.position.y < -3f)
            go.toRemove = true;
    }
}
