package com.monstrous.pixelwar.behaviours;

import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.World;

public class Behaviour {
    protected GameObject go;

    public Behaviour(GameObject go) {
        this.go = go;
    }

    public void update(World world, float deltaTime ) {

    }
}
