package com.monstrous.pixelwar.behaviours;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.GameObject;
import com.monstrous.pixelwar.Sounds;
import com.monstrous.pixelwar.World;

public class Tank extends Behaviour {

    public static final float TRACKING_RADIUS = 30f;
    public static final float FIRE_REPEAT = 1.2f;

    public static final float RADIUS = 3.12f;
    public static final float MUZZLE_HEIGHT = 2.18f;
    public static final float BULLET_SPEED = 8f;

    private float timeToFire;
    private float retargetTime;
    private GameObject target;
    private Vector3 fireVector = new Vector3();
    private Vector3 bulletVelocity = new Vector3();
    private Vector3 spawnPoint = new Vector3();

    public Tank(GameObject go) {
        super(go);
        timeToFire = FIRE_REPEAT;
        retargetTime = 1f;
    }

    @Override
    public void update( World world, float deltaTime ) {

        // death animation
        if (go.isDying) {
            go.velocity.set(0, -0.2f, 0);
            if (go.position.y < -5f)
                go.toRemove = true;
        }

        retargetTime -= deltaTime;          // retarget only every so often to avoid expensive call to closestEnemy()
        if (retargetTime < 0) {
            retargetTime = 1f;

            target = world.closestEnemy(go, TRACKING_RADIUS);
            if (target != null) {
                // recalculate the destination angle from current position
                fireVector.set(target.position).sub(go.position);    // unit vector towards destination
                fireVector.sub(0,MUZZLE_HEIGHT,0);  // compensate for muzzle height
                fireVector.nor();
                double phi = Math.atan2(fireVector.z, fireVector.x);     // angle in horizontal XZ plane (radians) used for turret rotation
                go.targetAngle = (float) (180f * phi / Math.PI);
            }
        }

        timeToFire -= deltaTime;
        if( !go.isDying && timeToFire < 0 && target != null) {
            timeToFire = FIRE_REPEAT;

            // spawn point: where bullet originates: at the end of the barrel
            spawnPoint.set(fireVector).scl(RADIUS).add(go.position);
            spawnPoint.y = go.position.y + MUZZLE_HEIGHT;
            bulletVelocity.set(fireVector).scl(BULLET_SPEED);

            world.spawnItem(go.army, "Bullet", spawnPoint, go.targetAngle, bulletVelocity);
            Sounds.playSound(Sounds.TANK_FIRE);
        }
    }
}
