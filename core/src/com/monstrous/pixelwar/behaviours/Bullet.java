package com.monstrous.pixelwar.behaviours;

import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.World;

public class Bullet extends Behaviour {

    public static final float ACCELERATION = 1f;

    public Bullet(GameObject go) {
        super(go);
    }

    @Override
    public void update( float deltaTime ) {
        go.velocity.y -= deltaTime* ACCELERATION;
        if(go.position.y < 0)
            go.toRemove= true;
        else {
            GameObject collider = World.testForCollision(go);
            if(collider != null) {
                go.toRemove = true; // remove bullet

                collider.healthPoints -= 10;
                if( collider.healthPoints <= 0 )
                    collider.isDying = true;
                    collider.healthPoints = 0;
            }
        }

    }
}
