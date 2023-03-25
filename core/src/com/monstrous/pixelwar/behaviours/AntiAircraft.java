package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.World;

public class AntiAircraft extends Behaviour {

    public static final float TRACKING_RADIUS = 40f;

    public static final float FIRE_REPEAT = 0.5f;
    public static final float MAX_SWING = 45f;  // degrees

    public static final float RADIUS = 1.5f;
    public static final float MUZZLE_HEIGHT = 1.5f;
    public static final float BULLET_SPEED = 4f;

    private float timeToFire;
    private float turretAngle;
    private int turretDir;
    private GameObject target;
    private Vector3 tmpVec = new Vector3();

    public AntiAircraft(GameObject go) {
        super(go);
        timeToFire = FIRE_REPEAT;
        turretAngle = 0;
        turretDir = 1;
    }

    @Override
    public void update( float deltaTime ) {
        // death animation
        if(go.isDying) {
            go.velocity.set(0, -0.2f, 0);
        }
        if(go.position.y < -1f)
            go.toRemove = true;


        target = World.closestEnemyAirship(go, TRACKING_RADIUS);
        if(target != null) {
            // recalculate the destination angle from current position
            tmpVec.set(target.position).sub(go.position).nor();    // unit vector towards destination
            double phi = Math.atan2(tmpVec.z, tmpVec.x);     // angle in horizontal XZ plane (radians)
            go.targetAngle = (float)(180f * phi/Math.PI);
        }


        turretAngle += turretDir * 10f * deltaTime;
        if(Math.abs(turretAngle) > MAX_SWING)       // swing to and fro
            turretDir *= -1;
        go.modelInstance2.transform.setToRotation(Vector3.Y, -(go.targetAngle+turretAngle)).trn(go.position); // update transform with rotation and position

        timeToFire -= deltaTime;
        if(timeToFire < 0 && target != null) {
            timeToFire = FIRE_REPEAT;

            double angleRads = (go.targetAngle+turretAngle)*Math.PI/180f;
            Vector3 spawnPoint = new Vector3(go.position);
            spawnPoint.y += MUZZLE_HEIGHT;
            spawnPoint.x += RADIUS * Math.cos(angleRads);
            spawnPoint.z += RADIUS * Math.sin(angleRads);
            Vector3 velocity = new Vector3(BULLET_SPEED*(float)Math.cos(angleRads), BULLET_SPEED/2, BULLET_SPEED*(float)Math.sin(angleRads));

            GameObject bullet = World.spawnItem(go.army.name, "Bullet", spawnPoint, go.targetAngle+turretAngle, velocity);
            //Gdx.app.log("Spawn missile", ""+spawnPoint);
        }


    }
}
