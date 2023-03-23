package com.monstrous.pixelwar;

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

    }
}
