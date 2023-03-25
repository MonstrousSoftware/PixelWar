package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.pixelwar.behaviours.*;

public class GameObject {
    public static float ROTATION_SPEED = 30f;

    public Army army;
    public GameObjectType type;
    public ModelInstance modelInstance;
    public ModelInstance modelInstance2;
    public Vector3 position;
    public Vector3 velocity;
    public float angle;     // around up axis (0 degrees is on the +X), models have to face forward on +X axis
    public float destAngle;
    public float targetAngle;
    public Vector3 destination;
    public boolean isMovingToDestination;
    public boolean isRotating;
    private Vector3 tmpVec;
    private float timeToLive;
    public boolean isDying;
    public boolean toRemove;
    private Behaviour behaviour;
    public float healthPoints;

    public GameObject(String armyName, String typeName, Vector3 position, float angle, Vector3 velocity) {

        this.army = Armies.findArmy(armyName);


        this.type = GameObjectTypes.findType(typeName);
        if(type == null)
            return;
        healthPoints = type.healthPoints;

        this.position = new Vector3(position);
        this.velocity = new Vector3(velocity);
        this.angle = angle;
        destination = new Vector3();
        isMovingToDestination = false;
        isRotating = false;
        toRemove = false;
        isDying = false;
        targetAngle = 60f;

        behaviour = null;
        if(type.name.contentEquals("Anti-Aircraft"))
            behaviour = new AntiAircraft(this);
        if(type.name.contentEquals("Tank"))
            behaviour = new TankBehaviour(this);
        if(type.name.contentEquals("Bullet"))
            behaviour = new Bullet(this);
        if(type.name.contentEquals("AirShip"))
            behaviour = new AirShip(this);
        if(type.name.contentEquals("Tower"))
            behaviour = new Tower(this);
        if(type.name.contentEquals("Flag"))
            behaviour = new Flag(this);

        Model model = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(model, type.modelName);
        modelInstance.transform.rotate(Vector3.Y, angle).trn(position);

        timeToLive = type.timeToLive;

        modelInstance2 = null;
        if(type.modelName2 != null) {
            modelInstance2 = new ModelInstance(model, type.modelName2);
            //modelInstance2.transform.translate(position);
            modelInstance2.transform.rotate(Vector3.Y, targetAngle).trn(position);
        }
        setArmy(army);
        tmpVec = new Vector3();
    }


    public void setDestination( Vector3 destination ){
        this.destination.set(destination);
        isMovingToDestination = true;
        isRotating = true;
    }

    public void update( float deltaTime ) {
        if(timeToLive > 0f){
            timeToLive -= deltaTime;
            if(timeToLive <= 0f)
                toRemove = true;    // mark for removal
        }
        if(isRotating) {
            // recalculate the destination angle from current position
            tmpVec.set(destination).sub(position).nor();    // unit vector towards destination
            double phi = Math.atan2(tmpVec.z, tmpVec.x);     // angle in horizontal XZ plane (radians)
            destAngle = (float)(180f * phi/Math.PI);

            // make sure to take the smallest arc to the destination, never more than 180 degrees
            if(destAngle - angle > 180f)
                destAngle -= 360f;
            if(destAngle - angle < -180f)
                destAngle += 360f;

            // rotate towards the destination angle
            if(destAngle > angle) {
                angle += ROTATION_SPEED*deltaTime;
                if(angle > destAngle) {     // don't overshoot
                    angle = destAngle;
                    isRotating = false;
                }
            }
            if(destAngle < angle) {
                angle -= ROTATION_SPEED*deltaTime;
                if(angle < destAngle) {
                    angle = destAngle;
                    isRotating = false;
                }
            }
        }


        if(isMovingToDestination) {
            float distance = position.dst2(destination);
            if (distance < 1f) {   // reached destination
                isMovingToDestination = false;
                Gdx.app.log("reached destination", "");
                velocity.set(0,0,0);
            }
            else {
                // don't move if we are facing away from the destination, just turn until we are facing more the right direction
                // todo smaller angle if distance is small
                if(Math.abs(angle - destAngle) < 15f) {
                    // move in direction that the unit is facing
                    velocity.set((float) Math.cos(angle * Math.PI / 180f), 0, (float) Math.sin(angle * Math.PI / 180f));
                    velocity.scl(type.maxSpeed);    // scale for speed and time step
                }
            }
        }

        tmpVec.set(velocity).scl(deltaTime);
        position.add(tmpVec);

        if(type.followsTerrain && !isDying)
            position.y = Terrain.getHeight(position.x, position.z);   // follow terrain height

        modelInstance.transform.setToRotation(Vector3.Y, -angle).trn(position); // update transform with rotation and position
        if (modelInstance2 != null)
            modelInstance2.transform.setToRotation(Vector3.Y, -targetAngle).trn(position);

        if(behaviour != null)
            behaviour.update(deltaTime);
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
