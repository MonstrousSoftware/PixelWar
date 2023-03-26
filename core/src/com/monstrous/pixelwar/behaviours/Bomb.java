package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.Gdx;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.Terrain;
import com.monstrous.pixelwar.World;

public class Bomb extends Behaviour {

    public static final float ACCELERATION = 1.5f;
    public static final float BOMB_RADIUS = 5f;

    public Bomb(GameObject go) {

        super(go);
    }

    @Override
    public void update( float deltaTime ) {
        go.velocity.y -= deltaTime* ACCELERATION;

        float ht = Terrain.getHeight(go.position.x, go.position.z);
        if(go.position.y + go.type.bbox.getCenterY() < ht) // hit the ground
        {
            go.toRemove= true;  // removee the bomb
            World.blastEffect(go, BOMB_RADIUS);
        }
    }
}
