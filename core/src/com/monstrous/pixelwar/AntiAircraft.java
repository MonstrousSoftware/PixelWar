package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class AntiAircraft extends Behaviour {

    public static final float FIRE_REPEAT = 0.5f;
    public static final float MAX_SWING = 45f;  // degrees

    public static final float RADIUS = .8f;
    public static final float MUZZLE_HEIGHT = 2f;
    public static final float BULLET_SPEED = 4f;

    private float timeToFire;
    private float turretAngle;
    private int turretDir;

    public AntiAircraft(GameObject go) {
        super(go);
        timeToFire = FIRE_REPEAT;
        turretAngle = 0;
        turretDir = 1;
    }

    @Override
    public void update( float deltaTime ) {
        turretAngle += turretDir * 10f * deltaTime;
        if(Math.abs(turretAngle) > MAX_SWING)       // swing to and fro
            turretDir *= -1;
        go.modelInstance2.transform.setToRotation(Vector3.Y, -(go.angle+turretAngle)).trn(go.position); // update transform with rotation and position

        timeToFire -= deltaTime;
        if(timeToFire < 0) {
            timeToFire = FIRE_REPEAT;

            double angleRads = (go.angle+turretAngle)*Math.PI/180f;
            Vector3 spawnPoint = new Vector3(go.position);
            spawnPoint.y += MUZZLE_HEIGHT;
            spawnPoint.x += RADIUS * Math.cos(angleRads);
            spawnPoint.z += RADIUS * Math.sin(angleRads);
            Vector3 velocity = new Vector3(BULLET_SPEED*(float)Math.cos(angleRads), BULLET_SPEED, BULLET_SPEED*(float)Math.sin(angleRads));

            GameObject bullet = World.spawnItem(go.army.name, "Bullet", spawnPoint, turretAngle, velocity);
            //Gdx.app.log("Spawn missile", ""+spawnPoint);
        }


    }
}
