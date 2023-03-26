package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.Gdx;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.Sounds;
import com.monstrous.pixelwar.World;

public class Bullet extends Behaviour {

    public static final float ACCELERATION = 0.1f;

    public Bullet(GameObject go) {
        super(go);
    }

    @Override
    public void update( float deltaTime ) {
        go.velocity.y -= deltaTime* ACCELERATION;
        if(go.position.y < -2f)     // bullets that fall (well) under terrain can be removed
            go.toRemove= true;
        else {
            GameObject collider = World.testForCollision(go);
            if(collider != null) {
                go.toRemove = true; // remove bullet
                Gdx.app.debug("bullet hits", collider.type.name+" hp:"+collider.healthPoints);
                collider.takeDamage(10);
            }
        }

    }
}
