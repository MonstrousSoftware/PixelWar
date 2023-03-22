package com.monstrous.pixelwar;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class GameObject {
    public GameObjectType type;
    public ModelInstance modelInstance;
    public ModelInstance modelInstance2;
    public Vector3 position;

    public GameObject(String typeName, Vector3 position, float angle) {

        this.type = GameObjectTypes.findType(typeName);
        if(type == null)
            return;

        this.position = new Vector3(position);


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
    }
}
