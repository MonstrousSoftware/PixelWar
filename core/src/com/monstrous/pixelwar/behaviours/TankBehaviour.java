package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.Sounds;
import com.monstrous.pixelwar.World;
import com.monstrous.pixelwar.behaviours.Behaviour;

public class TankBehaviour extends Behaviour {

    public static final float TRACKING_RADIUS = 30f;
    public static final float FIRE_REPEAT = 1.2f;

    public static final float RADIUS = 1.8f;
    public static final float MUZZLE_HEIGHT = 1.0f;
    public static final float BULLET_SPEED = 8f;

    private float timeToFire;
    private GameObject target;
    private Vector3 tmpVec = new Vector3();

    public TankBehaviour(GameObject go) {
        super(go);
        timeToFire = FIRE_REPEAT;
    }

    @Override
    public void update( float deltaTime ) {

        // death animation
        if(go.isDying)
            go.velocity.set(0,-0.2f,0);
        if(go.position.y < -5f)
            go.toRemove = true;

        target = World.closestEnemy(go, TRACKING_RADIUS);
        if(target != null) {
            // recalculate the destination angle from current position
            tmpVec.set(target.position).sub(go.position).nor();    // unit vector towards destination
            double phi = Math.atan2(tmpVec.z, tmpVec.x);     // angle in horizontal XZ plane (radians)
            go.targetAngle = (float)(180f * phi/Math.PI);
        }

        //go.modelInstance2.transform.setToRotation(Vector3.Y, -(go.angle+turretAngle)).trn(go.position); // update transform with rotation and position

        timeToFire -= deltaTime;
        if(timeToFire < 0 && target != null) {
            timeToFire = FIRE_REPEAT;

            double angleRads = (go.targetAngle)*Math.PI/180f;
            Vector3 spawnPoint = new Vector3(go.position);
            spawnPoint.y += MUZZLE_HEIGHT;
            spawnPoint.x += RADIUS * Math.cos(angleRads);
            spawnPoint.z += RADIUS * Math.sin(angleRads);
            Vector3 velocity = new Vector3(BULLET_SPEED*(float)Math.cos(angleRads), 0, BULLET_SPEED*(float)Math.sin(angleRads));

            GameObject bullet = World.spawnItem(go.army.name, "Bullet", spawnPoint, go.targetAngle, velocity);
            Sounds.playSound(Sounds.TANK_FIRE);
        }


    }
}
