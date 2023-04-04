package com.monstrous.pixelwar.behaviours;

import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.World;

public class Tower extends Behaviour {

    public Tower(GameObject go) {
        super(go);
    }

    @Override
    public void update(World world, float deltaTime ) {

        // death animation
        if(go.isDying) {
            go.modelInstance.transform.setFromEulerAngles(go.angle, 15f, 0).trn(go.position);
            go.velocity.set(0, -0.8f, 0);
        }
        if(go.position.y < -8f)
            go.toRemove = true;
    }
}
