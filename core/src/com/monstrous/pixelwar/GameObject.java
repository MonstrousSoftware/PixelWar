package com.monstrous.pixelwar;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class GameObject {
    public static float SPEED = 10f;

    public GameObjectType type;
    public ModelInstance modelInstance;
    public ModelInstance modelInstance2;
    public Vector3 position;
    public Vector3 destination;
    public boolean isMoving;
    private Vector3 tmpVec;

    public GameObject(String typeName, Vector3 position, float angle) {

        this.type = GameObjectTypes.findType(typeName);
        if(type == null)
            return;

        this.position = new Vector3(position);
        destination = new Vector3();
        isMoving = false;


        Model model = ModelAssets.getModel("Assets");
        modelInstance =  new ModelInstance(model, type.modelName);
        //float y = terrain.getHeight(x, z);
        modelInstance.transform.translate(position);
        modelInstance.transform.rotate(Vector3.Y, angle);

        modelInstance2 = null;
        if(type.modelName2 != null) {
            modelInstance2 = new ModelInstance(model, type.modelName2);
            modelInstance2.transform.translate(position);
            modelInstance2.transform.rotate(Vector3.Y, angle + 60f);
        }
        tmpVec = new Vector3();
    }


    public void setDestination( Vector3 destination ){
        this.destination.set(destination);
        isMoving = true;
    }

    public void update( float deltaTime ) {
        if(!isMoving)
            return;
        if(position.dst2(destination) < 1f) {   // reached destination
            isMoving = false;
            return;
        }
        tmpVec.set(destination);
        tmpVec.sub(position);           // vector towards destination
        tmpVec.nor();                   // make unit vector
        double angle = Math.atan2(tmpVec.y, tmpVec.x);
        float degrees = (float) (angle*180f/Math.PI);
        tmpVec.scl(SPEED*deltaTime);    // scale for speed and time step
        position.add(tmpVec);
        tmpVec.y = Terrain.getHeight(tmpVec.x, tmpVec.z);   // follow terrain height
        modelInstance.transform.setToRotation(Vector3.Y,  degrees);
        modelInstance.transform.setTranslation(position);
        if(modelInstance2 != null)
            modelInstance2.transform.setTranslation(position);

    }
}
