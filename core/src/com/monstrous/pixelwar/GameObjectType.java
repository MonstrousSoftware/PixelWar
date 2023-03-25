package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GameObjectType {
    public String name;
    public String modelName;
    public String modelName2;
    public Boolean isScenery;  // show this type in mini map? false for scenery
    public boolean isMobile;
    public boolean isProjectile;
    public boolean isAirship;
    public boolean isTower;
    public boolean followsTerrain;
    public float timeToLive;
    public float healthPoints;
    public Vector3 dimensions;
    public BoundingBox bbox;
    public float radius;
    public float maxSpeed;

    public GameObjectType(String name, String modelName, String modelName2, boolean isScenery, float healthPoints, float timeToLive) {
        this.name = name;
        this.modelName = modelName;
        this.modelName2 = modelName2;
        this.isScenery = isScenery;
        this.timeToLive = timeToLive;
        this.healthPoints = healthPoints;
        this.followsTerrain = true;
        this.isMobile = false;
        this.isProjectile = false;
        this.isAirship = false;
        this.isTower = false;
        this.maxSpeed = 5f;

        Model model = ModelAssets.getModel("Assets");
        ModelInstance modelInstance =  new ModelInstance(model, modelName);

        // calculate dimensions, e.g. for object picking
        bbox = new BoundingBox();
        modelInstance.calculateBoundingBox(bbox);
        dimensions = new Vector3();
        bbox.getDimensions(dimensions);
        float hw = dimensions.x/2f;
        float hd = dimensions.z/2f;
        radius = (float) Math.sqrt(hw*hw + hd*hd);      // radius in horizontal plane
        //Gdx.app.log(name, "radius: "+radius);
    }
}
