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
            Gdx.app.log("AirShip", "dying");
            go.modelInstance.transform.setFromEulerAngles(go.angle, 20f, 0).trn(go.position);
            go.isMovingToDestination = false;
            go.velocity.set(0, -0.8f, 0);
         }
        if(go.position.y < -5f)
            go.toRemove = true;
    }
}
