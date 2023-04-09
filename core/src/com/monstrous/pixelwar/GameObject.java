package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.behaviours.*;

public class GameObject {
    public Army army;
    public GameObjectType type;
    public ModelInstance modelInstance;
    public ModelInstance modelInstance2;
    public Vector3 position;
    public Vector3 velocity;
    public float acceleration;  // -1, 0 , 1
    public float angle;     // around up axis (0 degrees is on the +X), models have to face forward on +X axis
    private float prevAngle;
    public float destAngle;
    public float targetAngle;
    private float prevTargetAngle;
    public Vector3 destination;
    public boolean isMovingToDestination;
    public boolean isRotating;
    private Vector3 tmpVec;
    private float timeToLive;
    public boolean isDying;
    public boolean toRemove;
    public Behaviour behaviour;
    public float healthPoints;
    public Vector3 terrainNormal;

    public GameObject(Terrain terrain, Army army, GameObjectType type, Vector3 position, float angle, Vector3 velocity) {
        this.army = army;
        this.type = type;
        this.behaviour = null;

        if(type == null) {
            Gdx.app.error("null type in GameObject constructor","");
            return;
        }
        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.angle = angle;

        healthPoints = type.healthPoints;
        timeToLive = type.timeToLive;

        destination = new Vector3();
        isMovingToDestination = false;
        acceleration = 0;
        isRotating = false;
        toRemove = false;
        isDying = false;
        targetAngle = 6f;
        prevAngle = -999f;
        prevTargetAngle = -999f;

        Model modelAssets = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(modelAssets, type.modelName);
        modelInstance2 = null;
        if(type.modelName2 != null) {
            // some game object types have 2 model instances,e.g. a tank has a turret, airship has a bomb
            modelInstance2 = new ModelInstance(modelAssets, type.modelName2);
        }

        terrainNormal = new Vector3();
        terrain.getNormal(position.x, position.z, terrainNormal);               // normal vector of the terrain at game object position
        setTransform(modelInstance, position, terrainNormal, angle);
        if(modelInstance2 != null)
            setTransform(modelInstance2, position, terrainNormal, targetAngle);

        setArmy(army);
        tmpVec = new Vector3();
    }

    public void takeDamage( World world, int damage ) {
        healthPoints -= damage;
        Sounds.playSound(Sounds.BULLET_HIT);
        if( healthPoints <= 0 ) {
            isDying = true;
            healthPoints = 0;
            Sounds.playSound(Sounds.EXPLOSION);
            world.spawnFire(position.x, position.z);
        }
    }


    public void setDestination( Vector3 destination ){
        if(isDying)
            return;
        this.destination.set(destination);
        isMovingToDestination = true;
        isRotating = true;
        acceleration = 1f;
    }

    // go towards destination but stay at some distance from it
    // (e.g. in order not to have a tank stand on top of a flag)
    public void setDestination( Vector3 destination, float distance ){
        // calculate a position at 'distance' from 'destination' in the direction of the current position
        //
        tmpVec.set(position).sub(destination).nor().scl(distance);
        tmpVec.add(destination);
        setDestination(tmpVec);
    }

    public void update( World world, float deltaTime ) {
        if(timeToLive > 0f){
            timeToLive -= deltaTime;
            if(timeToLive <= 0f)
                toRemove = true;    // mark for removal
        }


        if(isRotating) {
            // recalculate the destination angle from current position
            tmpVec.set(destination).sub(position);
            double phi = Math.atan2(tmpVec.z, tmpVec.x);     // angle in horizontal XZ plane (radians)
            destAngle = (float)(180f * phi/Math.PI);

            // make sure to take the smallest arc to the destination, never more than 180 degrees
            if(destAngle - angle > 180f)
                destAngle -= 360f;
            if(destAngle - angle < -180f)
                destAngle += 360f;

            // rotate towards the destination angle
            if(destAngle > angle) {
                angle += type.rotationSpeed*deltaTime;
                if(angle > destAngle) {     // don't overshoot
                    angle = destAngle;
                    isRotating = false;
                }
            }
            if(destAngle < angle) {
                angle -= type.rotationSpeed*deltaTime;
                if(angle < destAngle) {
                    angle = destAngle;
                    isRotating = false;
                }
            }
          }


        if(isMovingToDestination && !isDying) {

            // compare position with destination in XZ plane, (ignore Y component for the sake of airships)
            tmpVec.set(destination);
            tmpVec.y = position.y;
            float distance = position.dst(tmpVec);

            // don't move if we are facing away from the destination, just turn until we are facing more the right direction
            // smaller angle if distance is small to avoid that we end up going round in circles around the destination
            float factor = distance/type.turnFactor;
            float maxAngle = 45f * factor;
            if(Math.abs(angle - destAngle) < maxAngle) {
                    acceleration = 4f;
            }
            else {  // while turning, slow down
                    acceleration = -4f;
            }
            if (distance < 1f) {   // reached destination
                isMovingToDestination = false;
                velocity.set(0,0,0);
                acceleration = 0f;
            }
            else if (distance < 5f) {   // close to destination, slow down
                acceleration = -8f;
            }

            float speed = velocity.len();
            if(speed < type.maxSpeed && acceleration > 0)
                speed += acceleration*deltaTime;
            if(speed > 0 && acceleration < 0)
                speed += acceleration*deltaTime;
            if(speed < 0)
                speed = 0;

            // move in direction that the unit is facing
            velocity.set((float) Math.cos(angle * Math.PI / 180f), 0, (float) Math.sin(angle * Math.PI / 180f));    // hmmm... too much trig
            velocity.scl(speed);// scale for speed and time step

        }
        float speed2 = velocity.len2();
        if(speed2 > 0.01f) {                            // if the speed is zero we can skip the next steps
            tmpVec.set(velocity).scl(deltaTime);
            position.add(tmpVec);

            if (type.followsTerrain && !isDying) {
                position.y = world.terrain.getHeight(position.x, position.z);   // follow terrain height
                world.terrain.getNormal(position.x, position.z, this.terrainNormal);    // get terrain surface normal
            }
        }

        if(speed2 > 0.01f || Math.abs(angle-prevAngle) > 0.01f ) {
            setTransform(modelInstance, position, terrainNormal, angle);
            prevAngle = angle;
        }
        if(modelInstance2 != null && (speed2 > 0.01f || Math.abs(targetAngle-prevTargetAngle) > 0.01f)) {
            setTransform(modelInstance2, position, terrainNormal, targetAngle);
            prevTargetAngle = targetAngle;
        }
        if(behaviour != null)
            behaviour.update(world, deltaTime);
    }

    static final Vector3 rotationAxis = new Vector3();
    static final Matrix4 directionalMatrix = new Matrix4();

    private void setTransform(ModelInstance instance, Vector3 position, Vector3 up, float angle) {

        rotationAxis.set(Vector3.Y).crs(up).nor();
        double tiltAngle = Math.acos(Vector3.Y.dot(up));
        instance.transform.setToRotationRad(rotationAxis, (float)tiltAngle);
        directionalMatrix.setToRotation(Vector3.Y, -angle);

        instance.transform.mul(directionalMatrix).trn(position);
    }

    public void setArmy( Army army ) {
        this.army = army;
        if ( !army.isNeutral && modelInstance != null) {                  // set material colour to match the faction colour
            Material mat = modelInstance.materials.get(0);
            mat.clear();
            mat.set(army.material);
            if(modelInstance2 != null) {
                mat = modelInstance2.materials.get(0);
                mat.clear();
                mat.set(army.material);
            }
        }
    }
}
