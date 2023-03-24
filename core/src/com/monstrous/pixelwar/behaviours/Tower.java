package com.monstrous.pixelwar.behaviours;

import com.monstrous.pixelwar.GameObject;

public class Tower extends Behaviour {

    public Tower(GameObject go) {
        super(go);
    }

    @Override
    public void update( float deltaTime ) {

        // death animation
        if(go.isDying)
            go.velocity.set(0,-0.8f,0);
        if(go.position.y < -5f)
            go.toRemove = true;
    }
}
