package com.monstrous.pixelwar.behaviours;

import com.monstrous.pixelwar.GameObject;

public class Flag extends Behaviour {

    public Flag(GameObject go) {
        super(go);
    }

    @Override
    public void update( float deltaTime ) {

        // death animation
        if(go.isDying) {
            go.modelInstance.transform.setFromEulerAngles(go.angle, 45f, 0).trn(go.position);
            go.velocity.set(0, -0.2f, 0);
        }
        if(go.position.y < -5f)
            go.toRemove = true;
    }
}
